package cd.go.plugin.config.yaml;

import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

public class JSONUtils {
    public static Object fromJSON(String json) {
        return new GsonBuilder().create().fromJson(json, Object.class);
    }

    public static String toJSON(Object object) {
        return new GsonBuilder().create().toJson(object);
    }

    public static void addOptionalValue(LinkedTreeMap<String, Object> dest, LinkedTreeMap<String, Object> src, String jsonField, String yamlFieldName) {
        if (src.containsKey(jsonField)) {
            dest.put(yamlFieldName, src.get(jsonField));
        }
    }
}