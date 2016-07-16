package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.YamlConfigException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import static cd.go.plugin.config.yaml.YamlUtils.addOptionalStringList;
import static cd.go.plugin.config.yaml.transforms.EnvironmentVariablesTransform.JSON_ENV_VAR_FIELD;

public class EnvironmentsTransform {
    private static final String JSON_ENV_NAME_FIELD = "name";
    private EnvironmentVariablesTransform environmentVariablesTransform;

    public EnvironmentsTransform(EnvironmentVariablesTransform environmentVariablesTransform){
        this.environmentVariablesTransform = environmentVariablesTransform;
    }

    public JsonObject transform(Object yamlObject) {
        Map<String,Object> map = (Map<String,Object>)yamlObject;
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            return transform(entry);
        }
        throw new RuntimeException("expected environments hash to have 1 item");
    }

    public JsonObject transform(Map.Entry<String, Object> env) {
        String envName = env.getKey();
        JsonObject envJson = new JsonObject();
        envJson.addProperty(JSON_ENV_NAME_FIELD,envName);
        Object envObj = env.getValue();
        if("".equals(envObj))
            return envJson;
        if(!(envObj instanceof Map))
            throw new YamlConfigException("Expected environment to be a hash");
        Map<String, Object> envMap = (Map<String,Object>)envObj;
        addOptionalStringList(envJson,envMap,"agents","agents");
        addOptionalStringList(envJson,envMap,"pipelines","pipelines");
        JsonArray jsonEnvVariables = environmentVariablesTransform.transform(envMap);
        if(jsonEnvVariables != null && jsonEnvVariables.size() > 0)
            envJson.add(JSON_ENV_VAR_FIELD,jsonEnvVariables);
        return envJson;
    }
}
