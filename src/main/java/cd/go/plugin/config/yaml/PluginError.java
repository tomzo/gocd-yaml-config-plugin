package cd.go.plugin.config.yaml;

import lombok.Getter;

@Getter
public class PluginError {
    private String location;
    private String message;

    public PluginError(String message, String location) {
        this.location = location;
        this.message = message;
    }
}
