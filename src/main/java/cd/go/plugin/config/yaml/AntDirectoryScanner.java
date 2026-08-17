package cd.go.plugin.config.yaml;

import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Arrays;

public class AntDirectoryScanner implements ConfigDirectoryScanner {

    @Override
    public String[] getFilesMatchingPattern(File directory, String pattern) {
        Path base = canonicalize(directory);

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(base.toFile());
        scanner.setIncludes(pattern.trim().split(" *, *"));
        scanner.addDefaultExcludes();
        scanner.scan();

        return Arrays.stream(scanner.getIncludedFiles())
                .filter(name -> resolvesInsideBase(base, name))
                .toArray(String[]::new);
    }

    /**
     * The scanner follows symlinks freely (including cycles, which the OS's symlink resolution
     * limit terminates), so keep only results that truly resolve within the repository —
     * symlinks must not be usable to read content from outside it.
     */
    private static boolean resolvesInsideBase(Path base, String name) {
        try {
            return base.resolve(name).toRealPath().startsWith(base);
        } catch (IOException e) {
            return false; // e.g. a broken symlink
        }
    }

    /**
     * Resolves symlinks in the base directory path itself so that the containment check
     * compares against the repository's real location.
     */
    private static Path canonicalize(File directory) {
        try {
            return directory.toPath().toRealPath();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
