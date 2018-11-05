package cd.go.plugin.config.yaml.transforms;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
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

    public LinkedTreeMap<String, Object> inverseTransform(List<LinkedTreeMap<String, Object>> vars) {
        if (vars == null)
            return null;
        LinkedTreeMap<String, Object> result = new LinkedTreeMap<>();
        LinkedTreeMap<String, Object> variables = new LinkedTreeMap<>();
        LinkedTreeMap<String, Object> secureVariables = new LinkedTreeMap<>();
        for (LinkedTreeMap<String, Object> var: vars) {
            if(var.containsKey(JSON_ENV_ENCRYPTED_FIELD)) {
                secureVariables.put((String) var.get(JSON_ENV_NAME_FIELD), var.get(JSON_ENV_ENCRYPTED_FIELD));
            } else {
                variables.put((String) var.get(JSON_ENV_NAME_FIELD), var.get(JSON_ENV_VALUE_FIELD));
            }

        }

        if (variables.size() > 0)
            result.put(YAML_ENV_VAR_FIELD, variables);
        if (secureVariables.size() > 0)
            result.put(YAML_SEC_VAR_FIELD, secureVariables);
        return result;
    }

    public JsonArray transform(Object all) {
        Map<String, Object> map = (Map<String, Object>) all;
        return transform(map.get(YAML_ENV_VAR_FIELD), map.get(YAML_SEC_VAR_FIELD));
    }
}
