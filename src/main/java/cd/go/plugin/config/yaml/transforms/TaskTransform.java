package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.YamlConfigException;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Map;

import static cd.go.plugin.config.yaml.YamlUtils.addOptionalBoolean;

public class TaskTransform {
    private static final String JSON_TASK_TYPE_FIELD = "type";
    private HashSet<String> yamlSpecialKeywords = new HashSet<>();

    public TaskTransform() {
        yamlSpecialKeywords.add("type");
        yamlSpecialKeywords.add("is_file");
    }

    public JsonObject transform(Object maybeTask) {
        Map<String,Object> map = (Map<String,Object>)maybeTask;
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            return transform(entry);
        }
        throw new RuntimeException("expected task hash to have 1 item");
    }

    public JsonObject transform(Map.Entry<String, Object> taskEntry) {
        JsonObject taskJson = new JsonObject();
        String taskType = taskEntry.getKey();
        taskJson.addProperty(JSON_TASK_TYPE_FIELD,taskType);

        if(!(taskEntry.getValue() instanceof Map))
            throw new YamlConfigException("expected task " + taskType + " to be hash");

        Map<String, Object> taskMap = (Map<String, Object>)taskEntry.getValue();

        addOptionalBoolean(taskJson,taskMap,"is_source_a_file","is_file");
        // copy all other members
        for(Map.Entry<String, Object> taskProp : taskMap.entrySet()) {
            if(yamlSpecialKeywords.contains(taskProp.getKey()))
                continue;
            if(taskProp.getValue() instanceof String)
                taskJson.addProperty(taskProp.getKey(),(String)taskProp.getValue());
        }
        return taskJson;
    }
}
