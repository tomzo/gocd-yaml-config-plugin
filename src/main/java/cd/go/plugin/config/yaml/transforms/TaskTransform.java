package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.YamlConfigException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.util.HashSet;
import java.util.Map;

import static cd.go.plugin.config.yaml.JSONUtils.addOptionalList;
import static cd.go.plugin.config.yaml.JSONUtils.addOptionalValue;
import static cd.go.plugin.config.yaml.YamlUtils.*;

public class TaskTransform extends ConfigurationTransform {
    private static final String JSON_TASK_TYPE_FIELD = "type";
    public static final String YAML_TASK_CANCEL_FIELD = "on_cancel";
    public static final String JSON_TASK_CANCEL_FIELD = "on_cancel";
    public static final String JSON_TASK_IS_FILE_FIELD = "is_source_a_file";
    public static final String YAML_TASK_IS_FILE_FIELD = "is_file";
    public static final String JSON_TASK_EXEC_ARGS_FIELD = "arguments";
    public static final String YAML_TASK_EXEC_ARGS_FIELD = "arguments";
    public static final String YAML_PLUGIN_STD_CONFIG_FIELD = "options";
    public static final String YAML_PLUGIN_SEC_CONFIG_FIELD = "secure_options";
    public static final String YAML_PLUGIN_CONFIGURATION_FIELD = "configuration";
    private static final String JSON_PLUGIN_CONFIGURATION_FIELD = "configuration";
    public static final String JSON_TASK_PLUGIN_CONFIGURATION_FIELD = "plugin_configuration";
    private HashSet<String> yamlSpecialKeywords = new HashSet<>();

    public TaskTransform() {
        yamlSpecialKeywords.add("type");
        yamlSpecialKeywords.add("is_file");
        yamlSpecialKeywords.add("on_cancel");
        yamlSpecialKeywords.add("arguments");
        yamlSpecialKeywords.add(YAML_PLUGIN_STD_CONFIG_FIELD);
        yamlSpecialKeywords.add(YAML_PLUGIN_SEC_CONFIG_FIELD);
        yamlSpecialKeywords.add(YAML_PLUGIN_CONFIGURATION_FIELD);
    }

    public JsonObject transform(Object maybeTask) {
        Map<String, Object> map = (Map<String, Object>) maybeTask;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            return transform(entry);
        }
        throw new RuntimeException("expected task hash to have 1 item");
    }

    public JsonObject transform(Map.Entry<String, Object> taskEntry) {
        JsonObject taskJson = new JsonObject();

        String taskType = taskEntry.getKey();
        if (taskEntry.getValue() == null) {
            taskJson.addProperty(JSON_TASK_TYPE_FIELD, taskType);
            return taskJson;
        }
        if (taskEntry.getValue() instanceof String) {
            if (taskType.equalsIgnoreCase("script")) {
                taskJson.addProperty(JSON_TASK_TYPE_FIELD, "plugin");
                JsonArray config = new JsonArray();
                JsonObject scriptKv = new JsonObject();
                scriptKv.addProperty("key", "script");
                scriptKv.addProperty("value", (String) taskEntry.getValue());
                config.add(scriptKv);
                JsonObject pluginConfig = new JsonObject();
                pluginConfig.addProperty("id", "script-executor");
                pluginConfig.addProperty("version", "1");
                taskJson.add(JSON_PLUGIN_CONFIGURATION_FIELD, config);
                taskJson.add(JSON_TASK_PLUGIN_CONFIGURATION_FIELD, pluginConfig);
                return taskJson;
            }
        }
        taskJson.addProperty(JSON_TASK_TYPE_FIELD, taskType);
        if (!(taskEntry.getValue() instanceof Map))
            throw new YamlConfigException("expected task " + taskType + " to be a hash");
        Map<String, Object> taskMap = (Map<String, Object>) taskEntry.getValue();
        addOnCancel(taskJson, taskMap);

        if ("fetch".equals(taskType)) {
            addOptionalObject(taskJson, taskMap, JSON_PLUGIN_CONFIGURATION_FIELD, YAML_PLUGIN_CONFIGURATION_FIELD);
            super.addConfiguration(taskJson, (Map<String, Object>) taskMap.get(JSON_PLUGIN_CONFIGURATION_FIELD));
        } else {
            addOptionalObject(taskJson, taskMap, JSON_TASK_PLUGIN_CONFIGURATION_FIELD, YAML_PLUGIN_CONFIGURATION_FIELD);
            super.addConfiguration(taskJson, taskMap);
        }


        addOptionalBoolean(taskJson, taskMap, JSON_TASK_IS_FILE_FIELD, YAML_TASK_IS_FILE_FIELD);
        addOptionalStringList(taskJson, taskMap, JSON_TASK_EXEC_ARGS_FIELD, YAML_TASK_EXEC_ARGS_FIELD);
        // copy all other members
        for (Map.Entry<String, Object> taskProp : taskMap.entrySet()) {
            if (yamlSpecialKeywords.contains(taskProp.getKey()))
                continue;
            if (taskProp.getValue() instanceof String)
                taskJson.addProperty(taskProp.getKey(), (String) taskProp.getValue());
        }
        return taskJson;
    }

    public LinkedTreeMap<String, Object> inverseTransform(LinkedTreeMap<String, Object> task) {
        String type = (String) task.get(JSON_TASK_TYPE_FIELD);
        LinkedTreeMap<String, Object> inverseTask = new LinkedTreeMap<>();
        LinkedTreeMap<String, Object> taskData = new LinkedTreeMap<>();

        addInverseOnCancel(taskData, task);

        addOptionalValue(taskData, task, JSON_TASK_PLUGIN_CONFIGURATION_FIELD, YAML_PLUGIN_CONFIGURATION_FIELD);
        addInverseConfiguration(taskData, task);

        addOptionalValue(taskData, task, JSON_TASK_IS_FILE_FIELD, YAML_TASK_IS_FILE_FIELD);
        addOptionalList(taskData, task, JSON_TASK_EXEC_ARGS_FIELD, YAML_TASK_EXEC_ARGS_FIELD);

        for (Map.Entry<String, Object> taskProp : task.entrySet()) {
            if (yamlSpecialKeywords.contains(taskProp.getKey()))
                continue;
            if (taskProp.getValue() instanceof String)
                taskData.put(taskProp.getKey(), taskProp.getValue());
        }

        inverseTask.put(type, taskData);
        return inverseTask;
    }

    private void addInverseOnCancel(LinkedTreeMap<String, Object> taskData, LinkedTreeMap<String, Object> task) {
        Object on_cancel = task.get(JSON_TASK_CANCEL_FIELD);
        if (on_cancel != null) {
            if (!(on_cancel instanceof LinkedTreeMap))
                throw new YamlConfigException("expected on_cancel task to be a hash");
            taskData.put(YAML_TASK_CANCEL_FIELD, inverseTransform((LinkedTreeMap<String, Object>) on_cancel));
        }
    }

    private void addOnCancel(JsonObject taskJson, Map<String, Object> taskMap) {
        Object on_cancel = taskMap.get(YAML_TASK_CANCEL_FIELD);
        if (on_cancel != null) {
            if (!(on_cancel instanceof Map))
                throw new YamlConfigException("expected on_cancel task to be a hash");
            JsonObject onCancelJson = transform(on_cancel);
            taskJson.add(JSON_TASK_CANCEL_FIELD, onCancelJson);
        }
    }
}
