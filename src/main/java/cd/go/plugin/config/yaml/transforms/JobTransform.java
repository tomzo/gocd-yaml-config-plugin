package cd.go.plugin.config.yaml.transforms;


import com.google.gson.JsonObject;

import java.util.Map;

public class JobTransform {
    public JsonObject transform(Map.Entry<String, Object> entry) {
        return transform(entry.getKey(),(Map<String, Object>)entry.getValue());
    }

    public JsonObject transform(String jobName, Map<String, Object> jobMap) {
        return null;
    }
}
