package cd.go.plugin.config.yaml;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.regex.Pattern;

public class YamlUtils {
    // http://yaml.org/type/bool.html
    private static Pattern truePattern = Pattern.compile(
            "y|Y|yes|Yes|YES|true|True|TRUE|on|On|ON");
    private static Pattern falsePattern = Pattern.compile(
            "n|N|no|No|NO|false|False|FALSE|off|Off|OFF");

    public static void addOptionalObject(JsonObject jsonObject, Map<String, Object> yamlSource, String jsonField, String yamlFieldName) {
        Object obj =  getOptionalObject(yamlSource, yamlFieldName);
        if(obj != null)
            jsonObject.add(jsonField,new Gson().toJsonTree(obj));
    }

    public static void addOptionalBoolean(JsonObject material, Map<String, Object> map, String jsonFieldName, String yamlFieldName) {
        Boolean autoUpdate = getOptionalBoolean(map, yamlFieldName);
        if(autoUpdate != null) {
            material.addProperty(jsonFieldName,autoUpdate);
        }
    }

    public static void addOptionalString(JsonObject jsonObject, Map<String, Object> yamlSource, String jsonField, String yamlFieldName) {
        String value =  getOptionalString(yamlSource, yamlFieldName);
        if(value != null)
            jsonObject.addProperty(jsonField,value);
    }

    public static void addRequiredString(JsonObject jsonObject, Map<String, Object> yamlSource, String jsonField, String yamlFieldName) {
        String value =  getOptionalString(yamlSource, yamlFieldName);
        if(value == null)
            throw new YamlConfigException("String field " + yamlFieldName + ": is required");

        jsonObject.addProperty(jsonField,value);
     }

    public static Boolean getOptionalBoolean(Map<String, Object> map, String fieldName) {
        Object value = map.get(fieldName);
        if(value != null)
        {
            String boolText = (String)value;
            if(truePattern.matcher(boolText).matches())
                return true;
            else if(falsePattern.matcher(boolText).matches())
                return false;
            throw new YamlConfigException("Expected boolean value in field " + fieldName + ", got " + boolText);
        }
        return null;
    }

    public static String getOptionalString(Map map, String fieldName) {
        Object value = map.get(fieldName);
        if(value != null)
        {
            return (String)value;
        }
        return null;
    }

    public static Object getOptionalObject(Map map, String fieldName) {
        return map.get(fieldName);
    }
}
