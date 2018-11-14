package cd.go.plugin.config.yaml;

public class PluginSettings {
    private String filePattern;
    private String generatorConfigPattern;;

    public PluginSettings() {
    }

    public PluginSettings(String filePattern, String generatorConfigPattern) {
        this.filePattern = filePattern;
        this.generatorConfigPattern = generatorConfigPattern;
    }

    public String getFilePattern() {
        return filePattern;
    }

    public String getGeneratorConfigPattern() {
        return generatorConfigPattern;
    }
}
