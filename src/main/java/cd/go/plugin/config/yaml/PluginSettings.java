package cd.go.plugin.config.yaml;

import lombok.Getter;

@Getter
public class PluginSettings {
    private String filePattern;

    public PluginSettings() {
    }

    public PluginSettings(String filePattern) {
        this.filePattern = filePattern;
    }

    public String getFilePattern() {
        return filePattern;
    }
}