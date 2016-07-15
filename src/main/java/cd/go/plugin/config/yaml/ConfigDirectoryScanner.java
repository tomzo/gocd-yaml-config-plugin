package cd.go.plugin.config.yaml;

import java.io.File;

public interface ConfigDirectoryScanner {
    String[] getFilesMatchingPattern(File directory, String pattern);
}