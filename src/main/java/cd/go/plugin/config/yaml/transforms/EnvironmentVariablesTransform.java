package cd.go.plugin.config.yaml.transforms;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Map;

public class EnvironmentVariablesTransform {
    public static final String JSON_ENV_VAR_FIELD = "environment_variables";

    public static final String JSON_ENV_NAME_FIELD = "name";
    public static final String JSON_ENV_VALUE_FIELD = "value";
    public static final String JSON_ENV_ENCRYPTED_FIELD = "encrypted_value";

    public static final String YAML_ENV_VAR_FIELD = "environment_variables";
    public static final String YAML_SEC_VAR_FIELD = "secure_variables";

    public JsonArray transform(Object variables, Object secureVariables) {
        JsonArray array = new JsonArray();
        if (variables != null && variables != "") {
            for (Map.Entry<String, String> env : ((Map<String, String>) variables).entrySet()) {
                JsonObject evarJson = new JsonObject();
                evarJson.addProperty(JSON_ENV_NAME_FIELD, env.getKey());
                evarJson.addProperty(JSON_ENV_VALUE_FIELD, env.getValue());
                array.add(evarJson);
            }
        }
        if (secureVariables != null && secureVariables != "") {
            for (Map.Entry<String, String> env : ((Map<String, String>) secureVariables).entrySet()) {
                JsonObject evarJson = new JsonObject();
                evarJson.addProperty(JSON_ENV_NAME_FIELD, env.getKey());
                evarJson.addProperty(JSON_ENV_ENCRYPTED_FIELD, env.getValue());
                array.add(evarJson);
            }
        }
        return array;
    }

    public JsonArray transform(Object all) {
        Map<String, Object> map = (Map<String, Object>) all;
        return transform(map.get(YAML_ENV_VAR_FIELD), map.get(YAML_SEC_VAR_FIELD));
    }
}
