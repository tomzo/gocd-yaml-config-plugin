package cd.go.plugin.config.yaml;

import org.apache.tools.ant.DirectoryScanner;

import java.io.File;

public class AntDirectoryScanner implements ConfigDirectoryScanner {

    @Override
    public String[] getFilesMatchingPattern(File directory, String pattern) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(directory);
        scanner.setIncludes(pattern.trim().split(" *, *"));
        scanner.scan();
        return scanner.getIncludedFiles();
    }
}