package cd.go.plugin.config.yaml;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestUtils {
    private static final Gson GSON = new Gson();

    public static JsonElement readJsonObject(String path) {
        JsonParser parser = new JsonParser();
        return parser.parse(TestUtils.createReader(path));
    }

    public static List<Map<String, Object>> readJsonArrayGson(String path) {
        return fromJson(TestUtils.createReader(path));
    }

    public static Map<String, Object> readJsonGson(String path) {
        return fromJson(TestUtils.createReader(path));
    }

    public static Object readYamlObject(String path) throws IOException {
        YamlConfig config = new YamlConfig();
        config.setAllowDuplicates(false);
        YamlReader reader = new YamlReader(TestUtils.createReader(path), config);
        return reader.read();
    }

    private static InputStreamReader createReader(String path) {
        final InputStream resourceAsStream = getResourceAsStream(path);
        return new InputStreamReader(resourceAsStream);
    }

    public static String loadString(String path) throws IOException {
        final InputStream resourceAsStream = getResourceAsStream(path);
        return IOUtils.toString(resourceAsStream);
    }

    static InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? TestUtils.class.getResourceAsStream(resource) : in;
    }

    public static void assertYamlEquivalent(String expected, String actual) {
        Yaml yaml = new Yaml();
        Object expectedParse = yaml.load(expected);
        Object actualParse = yaml.load(actual);
        assertEquals(expectedParse, actualParse);
    }

    private static <T> T fromJson(Reader json) {
        return GSON.fromJson(json, new TypeToken<T>() {}.getType());
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static String normalizePath(File directory) {
            return normalizePath(directory.getAbsolutePath());
    }

    public static String normalizePath(String directory) {
        String directoryString = directory;

        if (System.getProperty("os.name").contains("Windows")) {
            directoryString = directoryString.replaceAll("\\\\", "/");
        }
        return directoryString;
    }

}
