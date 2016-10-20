package cd.go.plugin.config.yaml;

public class PluginError {
    private String location;
    private String message;

    public PluginError() {
    }

    public PluginError(String message) {
        this.message = message;
    }

    public PluginError(String message, String location) {
        this.location = location;
        this.message = message;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
