package cd.go.plugin.config.yaml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class JSONUtils {

    private static final Gson GSON = new GsonBuilder().create();

    static Map<String, String> fromJSON(String json) {
        return GSON.fromJson(json, new TypeToken<Map<String, String>>() {}.getType());
    }

    static String toJSON(Object object) {
        return GSON.toJson(object);
    }

    public static void addOptionalValue(Map<String, Object> dest, Map<String, Object> src, String jsonField, String yamlFieldName) {
        if (src.containsKey(jsonField)) {
            dest.put(yamlFieldName, src.get(jsonField));
        }
    }

    public static void addOptionalList(Map<String, Object> dest, Map<String, Object> src, String jsonField, String yamlFieldName) {
        List<Object> value = (List<Object>) src.get(jsonField);
        if (value != null && !value.isEmpty()) {
            dest.put(yamlFieldName, value);
        }
    }

    public static void addOptionalInt(Map<String, Object> dest, Map<String, Object> src, String jsonField, String yamlFieldName) {
        Object n = src.get(jsonField);
        if (n == null)
            return;
        if (n instanceof Double) {
            dest.put(yamlFieldName, ((Double) n).intValue());
        } else {
            dest.put(yamlFieldName, n);
        }
    }

    public static void addRequiredValue(Map<String, Object> dest, Map<String, Object> src, String jsonField, String yamlFieldName) {
        Object value = src.get(jsonField);
        if (value == null)
            throw new YamlConfigException("field " + yamlFieldName + ": is required");

        dest.put(yamlFieldName, value);
    }
}