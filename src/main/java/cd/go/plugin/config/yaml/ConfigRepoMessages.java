package cd.go.plugin.config.yaml;

public interface ConfigRepoMessages {
    String REQ_GET_PLUGIN_SETTINGS = "go.processor.plugin-settings.get";
    String REQ_GET_CAPABILITIES = "get-capabilities";
    String REQ_PLUGIN_SETTINGS_CHANGED = "go.plugin-settings.plugin-settings-changed";
    String REQ_PLUGIN_SETTINGS_GET_CONFIGURATION = "go.plugin-settings.get-configuration";
    String REQ_PLUGIN_SETTINGS_GET_VIEW = "go.plugin-settings.get-view";
    String REQ_PLUGIN_SETTINGS_VALIDATE_CONFIGURATION = "go.plugin-settings.validate-configuration";
    String REQ_PARSE_DIRECTORY = "parse-directory";
    String REQ_PARSE_CONTENT = "parse-content";
    String REQ_PIPELINE_EXPORT = "pipeline-export";
    String REQ_GET_ICON = "get-icon";
}
