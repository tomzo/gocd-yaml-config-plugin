package cd.go.plugin.config.yaml;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class YamlUtils {
    // http://yaml.org/type/bool.html
    private static final Pattern PATTERN_TRUE = Pattern.compile(
            "y|Y|yes|Yes|YES|true|True|TRUE|on|On|ON");
    private static final Pattern PATTERN_FALSE = Pattern.compile(
            "n|N|no|No|NO|false|False|FALSE|off|Off|OFF");

    public static String dump(Object o) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        return new Yaml(options).dump(o);
    }

    public static void addOptionalObject(JsonObject jsonObject, Map<String, Object> yamlSource, String jsonField, String yamlFieldName) {
        Object obj = getOptionalObject(yamlSource, yamlFieldName);
        if (obj != null)
            jsonObject.add(jsonField, new Gson().toJsonTree(obj));
    }

    public static void addOptionalBoolean(JsonObject material, Map<String, Object> map, String jsonFieldName, String yamlFieldName) {
        Boolean autoUpdate = getOptionalBoolean(map, yamlFieldName);
        if (autoUpdate != null) {
            material.addProperty(jsonFieldName, autoUpdate);
        }
    }

    public static void addOptionalString(JsonObject jsonObject, Map<String, Object> yamlSource, String jsonField, String yamlFieldName) {
        String value = getOptionalString(yamlSource, yamlFieldName);
        if (value != null)
            jsonObject.addProperty(jsonField, value);
    }

    public static void addOptionalInteger(JsonObject jsonObject, Map<String, Object> yamlSource, String jsonField, String yamlFieldName) {
        Integer value = getOptionalInteger(yamlSource, yamlFieldName);
        if (value != null)
            jsonObject.addProperty(jsonField, value);
    }

    public static void addOptionalStringList(JsonObject jsonObject, Map<String, Object> yamlSource, String jsonField, String yamlFieldName) {
        JsonArray value = getOptionalStringList(yamlSource, yamlFieldName);
        if (value != null)
            jsonObject.add(jsonField, value);
    }

    private static JsonArray getOptionalStringList(Map<String, Object> map, String fieldName) {
        JsonArray jsonArray = new JsonArray();
        Object value = map.get(fieldName);
        if (value != null) {
            @SuppressWarnings("unchecked") List<String> list = (List<String>) value;
            if (list.isEmpty())
                return null;
            for (String item : list) {
                jsonArray.add(item);
            }
            return jsonArray;
        }
        return null;
    }

    public static void addRequiredString(JsonObject jsonObject, Map<String, Object> yamlSource, String jsonField, String yamlFieldName) {
        String value = getOptionalString(yamlSource, yamlFieldName);
        if (value == null)
            throw new YamlConfigException("String field " + yamlFieldName + ": is required");

        jsonObject.addProperty(jsonField, value);
    }

    public static Boolean getOptionalBoolean(Map<String, Object> map, String fieldName) {
        Object value = map.get(fieldName);
        if (value != null) {
            if (value instanceof Boolean)
                return (Boolean) value;
            String boolText = (String) value;
            if (PATTERN_TRUE.matcher(boolText).matches())
                return true;
            else if (PATTERN_FALSE.matcher(boolText).matches())
                return false;
            throw new YamlConfigException("Expected boolean value in field " + fieldName + ", got " + boolText);
        }
        return null;
    }

    public static String getOptionalString(Map<String, Object> map, String fieldName) {
        Object value = map.get(fieldName);
        if (value != null) {
            return (String) value;
        }
        return null;
    }

    public static Integer getOptionalInteger(Map<String, Object> map, String fieldName) {
        Object value = map.get(fieldName);
        if (value != null) {
            if (value instanceof Integer)
                return (Integer) value;
            else if (value instanceof String) {
                return parseInt((String) value);
            }
        }
        return null;
    }

    public static Object getOptionalObject(Map<String, Object> map, String fieldName) {
        return map.get(fieldName);
    }
}
