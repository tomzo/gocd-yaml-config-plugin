package cd.go.plugin.config.yaml.transforms;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Map;

public class EnvironmentVariablesTransform extends ConfigurationTransform {
    public static final String JSON_ENV_VAR_FIELD = "environment_variables";

    public static final String JSON_ENV_NAME_FIELD = "name";

    public static final String YAML_ENV_VAR_FIELD = "environment_variables";
    public static final String YAML_SEC_VAR_FIELD = "secure_variables";

    public JsonArray transform(Object variables, Object secureVariables) {
        JsonArray array = new JsonArray();
        transformPlainAndSecureOptions(array, variables, secureVariables, JSON_ENV_NAME_FIELD);
        return array;
    }

    public JsonArray transform(Object all) {
        Map<String, Object> map = (Map<String, Object>) all;
        return transform(map.get(YAML_ENV_VAR_FIELD), map.get(YAML_SEC_VAR_FIELD));
    }
}
