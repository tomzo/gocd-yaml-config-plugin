package cd.go.plugin.config.yaml.transforms;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Map;

public class ConfigurationTransform {

    private static final String JSON_PLUGIN_CONFIG_KEY_FIELD = "key";
    private static final String JSON_PLUGIN_CONFIG_VALUE_FIELD = "value";
    private static final String JSON_PLUGIN_CONFIG_ENCRYPTED_VALUE_FIELD = "encrypted_value";
    private static final String JSON_PLUGIN_CONFIGURATION_FIELD = "configuration";
    public static final String YAML_PLUGIN_STD_CONFIG_FIELD = "options";
    public static final String YAML_PLUGIN_SEC_CONFIG_FIELD = "secure_options";


    void addConfiguration(JsonObject json, Map<String, Object> configurationMap) {
        if (configurationMap == null) {
            return;
        }
        JsonArray configuration = new JsonArray();
        Object options = configurationMap.get(YAML_PLUGIN_STD_CONFIG_FIELD);
        Object optionsSecure = configurationMap.get(YAML_PLUGIN_SEC_CONFIG_FIELD);
        transformPlainAndSecureOptions(configuration, options, optionsSecure, JSON_PLUGIN_CONFIG_KEY_FIELD);
        if (configuration.size() > 0)
            json.add(JSON_PLUGIN_CONFIGURATION_FIELD, configuration);
    }

    void transformPlainAndSecureOptions(JsonArray configuration, Object options, Object secureOptions, String keyField) {
        if (options != null && options != "") {
            transformValues(configuration, (Map<String,String>) options, JSON_PLUGIN_CONFIG_VALUE_FIELD, keyField);
        }
        if (secureOptions != null && secureOptions != "") {
            transformValues(configuration, (Map<String, String>) secureOptions, JSON_PLUGIN_CONFIG_ENCRYPTED_VALUE_FIELD, keyField);
        }
    }

    private void transformValues(JsonArray configuration, Map<String, String> plainOrSecureOptions, String valueOrEncryptedValue, String keyField) {
        for (Map.Entry<String, String> env : plainOrSecureOptions.entrySet()) {
            JsonObject kv = new JsonObject();
            kv.addProperty(keyField, env.getKey());
            kv.addProperty(valueOrEncryptedValue, env.getValue());
            configuration.add(kv);
        }
    }
}
