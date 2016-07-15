package cd.go.plugin.config.yaml;
public class PluginSettings {
    private String filePattern;

    public PluginSettings()
    {
    }
    public PluginSettings(String filePattern)
    {
        this.filePattern = filePattern;
    }

    public String getFilePattern() {
        return filePattern;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }
}