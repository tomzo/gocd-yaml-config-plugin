package cd.go.plugin.config.yaml.transforms;

import com.google.gson.JsonElement;

import java.util.Map;

public class EnvironmentsTransform {
    private EnvironmentVariablesTransform environmentVariablesTransform;

    public EnvironmentsTransform(EnvironmentVariablesTransform environmentVariablesTransform){
        this.environmentVariablesTransform = environmentVariablesTransform;
    }

    public JsonElement transform(Map.Entry<String, Object> env) {
        return  null;
    }
}
