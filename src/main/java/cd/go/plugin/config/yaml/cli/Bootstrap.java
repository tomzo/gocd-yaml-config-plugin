package cd.go.plugin.config.yaml.cli;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Entry point for {@code java -jar}. The plugin jar bundles its dependencies as nested jars under
 * {@code lib/} (which GoCD puts on the plugin classpath, but a plain JVM cannot load), so this
 * class — which must only depend on the JDK — reads the nested jars' entries into memory and
 * launches {@code YamlPluginCli} through a classloader that serves them.
 */
public class Bootstrap {
    private static final String LIB_PREFIX = "lib/";

    public static void main(String[] args) throws Throwable {
        Path self = Path.of(Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        if (!Files.isRegularFile(self)) {
            // Not running from a jar (IDE, tests); dependencies are already on the classpath
            launch(Bootstrap.class.getClassLoader(), args);
            return;
        }

        // Parent past the application classloader, otherwise plugin classes would resolve there
        // and be unable to link against the nested dependencies
        ClassLoader loader = new NestedJarClassLoader(self.toUri().toURL(), readNestedJarEntries(self));
        Thread.currentThread().setContextClassLoader(loader);
        launch(loader, args);
    }

    private static Map<String, byte[]> readNestedJarEntries(Path self) throws IOException {
        try (JarFile jar = new JarFile(self.toFile())) {
            return jar.stream()
                    .filter(entry -> !entry.isDirectory())
                    .filter(entry -> entry.getName().startsWith(LIB_PREFIX) && entry.getName().endsWith(".jar"))
                    .map(nestedJar -> readJarEntries(jar, nestedJar))
                    .flatMap(entries -> entries.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (first, duplicate) -> first));
        }
    }

    private static Map<String, byte[]> readJarEntries(JarFile jar, JarEntry nestedJar) {
        Map<String, byte[]> entries = new HashMap<>();
        try (ZipInputStream in = new ZipInputStream(jar.getInputStream(nestedJar))) {
            for (ZipEntry entry = in.getNextEntry(); entry != null; entry = in.getNextEntry()) {
                if (!entry.isDirectory()) {
                    entries.put(entry.getName(), in.readAllBytes());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return entries;
    }

    private static void launch(ClassLoader loader, String[] args) throws Throwable {
        try {
            // Referenced by name so the class is only ever loaded through the given loader
            Class.forName("cd.go.plugin.config.yaml.cli.YamlPluginCli", true, loader)
                    .getMethod("main", String[].class)
                    .invoke(null, (Object) args);
        } catch (InvocationTargetException e) {
            throw e.getCause() != null ? e.getCause() : e;
        }
    }

    /**
     * Loads the plugin's own classes from the outer jar and dependency classes/resources from the
     * in-memory nested jar entries.
     */
    private static class NestedJarClassLoader extends URLClassLoader {
        private final Map<String, byte[]> nestedEntries;

        NestedJarClassLoader(URL selfJar, Map<String, byte[]> nestedEntries) {
            super(new URL[]{selfJar}, ClassLoader.getPlatformClassLoader());
            this.nestedEntries = nestedEntries;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                return super.findClass(name);
            } catch (ClassNotFoundException e) {
                byte[] bytes = nestedEntries.get(name.replace('.', '/') + ".class");
                if (bytes == null) {
                    throw e;
                }
                return defineClass(name, bytes, 0, bytes.length);
            }
        }

        @Override
        public URL findResource(String name) {
            URL url = super.findResource(name);
            return url != null ? url : nestedResourceUrl(name);
        }

        @Override
        public Enumeration<URL> findResources(String name) throws IOException {
            return Collections.enumeration(Stream.concat(
                    Collections.list(super.findResources(name)).stream(),
                    Stream.ofNullable(nestedResourceUrl(name))
            ).toList());
        }

        private URL nestedResourceUrl(String name) {
            byte[] bytes = nestedEntries.get(name);
            if (bytes == null) {
                return null;
            }
            try {
                return URL.of(new URI("nested", null, "/" + name, null), new BytesUrlStreamHandler(bytes));
            } catch (MalformedURLException | URISyntaxException e) {
                return null;
            }
        }
    }

    private static class BytesUrlStreamHandler extends URLStreamHandler {
        private final byte[] bytes;

        BytesUrlStreamHandler(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        protected URLConnection openConnection(URL url) {
            return new URLConnection(url) {
                @Override
                public void connect() {
                }

                @Override
                public InputStream getInputStream() {
                    return new ByteArrayInputStream(bytes);
                }
            };
        }
    }
}
