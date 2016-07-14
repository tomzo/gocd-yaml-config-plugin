package cd.go.plugin.config.yaml;

import java.util.Map;
import java.util.regex.Pattern;

public class YamlUtils {
    // http://yaml.org/type/bool.html
    private static Pattern truePattern = Pattern.compile(
            "y|Y|yes|Yes|YES|true|True|TRUE|on|On|ON");
    private static Pattern falsePattern = Pattern.compile(
            "n|N|no|No|NO|false|False|FALSE|off|Off|OFF");

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
}
