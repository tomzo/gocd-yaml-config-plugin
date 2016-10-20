package cd.go.plugin.config.yaml;

public class YamlConfigException extends RuntimeException {
    public YamlConfigException(String message) {
        super(message);
    }

    public YamlConfigException(String message, Throwable e) {
        super(message, e);
    }
}
