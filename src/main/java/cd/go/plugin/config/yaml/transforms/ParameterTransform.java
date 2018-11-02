package cd.go.plugin.config.yaml.transforms;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.util.Map;

public class ParameterTransform {

    public static final String YAML_PIPELINE_PARAMETERS_FIELD = "parameters";
    public static final String JSON_PARAM_NAME_FIELD = "name";
    public static final String JSON_PARAM_VALUE_FIELD = "value";


    public JsonArray transform(Object all) {
        Map<String, Object> map = (Map<String, Object>) all;
        Object parameters = map.get(YAML_PIPELINE_PARAMETERS_FIELD);
        JsonArray paramArray = new JsonArray();
        if (parameters != null && parameters != "") {
            for (Map.Entry<String, String> param : ((Map<String, String>) parameters).entrySet()) {
                JsonObject paramJson = new JsonObject();
                paramJson.addProperty(JSON_PARAM_NAME_FIELD, param.getKey());
                paramJson.addProperty(JSON_PARAM_VALUE_FIELD, param.getValue());
                paramArray.add(paramJson);
            }

        }
        return paramArray;
    }
}


