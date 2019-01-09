package cd.go.plugin.config.yaml;

import java.util.Map;

class PluginSettings {
    static final String PLUGIN_SETTINGS_FILE_PATTERN = "file_pattern";
    static final String DEFAULT_FILE_PATTERN = "**/*.gocd.yaml,**/*.gocd.yml";

    private String filePattern;

    PluginSettings() {
    }

    PluginSettings(String filePattern) {
        this.filePattern = filePattern;
    }

    static PluginSettings fromJson(String json) {
        Map<String, String> raw = JSONUtils.fromJSON(json);
        return new PluginSettings(raw.get(PLUGIN_SETTINGS_FILE_PATTERN));
    }

    String getFilePattern() {
        return filePattern;
    }
}