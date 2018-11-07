package cd.go.plugin.config.yaml;

import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

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

    public static void addOptionalList(LinkedTreeMap<String, Object> dest, LinkedTreeMap<String, Object> src, String jsonField, String yamlFieldName) {
        List<Object> value = (List<Object>) src.get(jsonField);
        if (value != null && !value.isEmpty()) {
            dest.put(yamlFieldName, value);
        }
    }

    public static void addOptionalInt(LinkedTreeMap<String, Object> dest, LinkedTreeMap<String, Object> src, String jsonField, String yamlFieldName) {
        Object n = src.get(jsonField);
        if (n == null)
            return;
        if (n instanceof Double) {
            dest.put(yamlFieldName, ((Double) n ).intValue());
        } else {
            dest.put(yamlFieldName, (Integer) n);
        }

    }

    public static void addRequiredValue(LinkedTreeMap<String, Object> dest, LinkedTreeMap<String, Object> src, String jsonField, String yamlFieldName) {
        Object value = src.get(jsonField);
        if (value == null)
            throw new YamlConfigException("field " + yamlFieldName + ": is required");

        dest.put(yamlFieldName, value);
    }
}