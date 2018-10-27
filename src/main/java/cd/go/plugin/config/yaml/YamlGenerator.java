package cd.go.plugin.config.yaml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class YamlGenerator {
    private final String name;
    private final String pattern;
    private final String script;

    private final static boolean isWindows =
        System.getProperty("os.name").toLowerCase().startsWith("windows");

    private final AntDirectoryScanner scanner = new AntDirectoryScanner();

    public YamlGenerator(String name, String pattern, String script) {
        this.name = name;
        this.pattern = pattern;
        this.script = script;
    }

    public List<String> generateFiles(File baseDir) throws Exception {
        String[] inputFiles = scanner.getFilesMatchingPattern(baseDir, pattern);
        List<String> outputFiles = new ArrayList<>();
        for (String file : inputFiles) {
            String outputFile = file + ".out";
            String absPath = new File(baseDir, file).getAbsolutePath();
            ProcessBuilder builder = new ProcessBuilder();
            String instance = script.replace("{{file}}", absPath);
            if (isWindows) {
                builder.command("cmd.exe", "/c", instance);
            } else {
                builder.command("sh", "-c", instance);
            }
            builder.redirectOutput(new File(baseDir, outputFile));
            builder.redirectError(new File(absPath + ".err"));
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0)
               outputFiles.add(outputFile);
        }
        return outputFiles;
    }
}
