package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.YamlConfigException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cd.go.plugin.config.yaml.JSONUtils.addOptionalInt;
import static cd.go.plugin.config.yaml.JSONUtils.addOptionalValue;
import static cd.go.plugin.config.yaml.YamlUtils.*;
import static cd.go.plugin.config.yaml.transforms.EnvironmentVariablesTransform.JSON_ENV_VAR_FIELD;
import static cd.go.plugin.config.yaml.transforms.ParameterTransform.YAML_PIPELINE_PARAMETERS_FIELD;

public class PipelineTransform {
    private static final String JSON_PIPELINE_NAME_FIELD = "name";
    private static final String JSON_PIPELINE_GROUP_FIELD = "group";
    private static final String JSON_PIPELINE_TEMPLATE_FIELD = "template";
    private static final String JSON_PIPELINE_LABEL_TEMPLATE_FIELD = "label_template";
    private static final String JSON_PIPELINE_PIPE_LOCKING_FIELD = "enable_pipeline_locking";
    private static final String JSON_PIPELINE_LOCK_BEHAVIOR_FIELD = "lock_behavior";
    private static final String JSON_PIPELINE_TRACKING_TOOL_FIELD = "tracking_tool";
    private static final String JSON_PIPELINE_TIMER_FIELD = "timer";
    private static final String JSON_PIPELINE_MATERIALS_FIELD = "materials";
    private static final String JSON_PIPELINE_STAGES_FIELD = "stages";
    private static final String JSON_PIPELINE_DISPLAY_ORDER_FIELD = "display_order_weight";

    private static final String YAML_PIPELINE_GROUP_FIELD = "group";
    private static final String YAML_PIPELINE_TEMPLATE_FIELD = "template";
    private static final String YAML_PIPELINE_LABEL_TEMPLATE_FIELD = "label_template";
    private static final String YAML_PIPELINE_PIPE_LOCKING_FIELD = "locking";
    private static final String YAML_PIPELINE_LOCK_BEHAVIOR_FIELD = "lock_behavior";
    private static final String YAML_PIPELINE_TRACKING_TOOL_FIELD = "tracking_tool";
    private static final String YAML_PIPELINE_TIMER_FIELD = "timer";
    private static final String YAML_PIPELINE_MATERIALS_FIELD = "materials";
    private static final String YAML_PIPELINE_STAGES_FIELD = "stages";
    private static final String YAML_PIPELINE_DISPLAY_ORDER_FIELD = "display_order";

    private final MaterialTransform materialTransform;
    private final StageTransform stageTransform;
    private final EnvironmentVariablesTransform variablesTransform;
    private ParameterTransform parameterTransform;

    public PipelineTransform(MaterialTransform materialTransform, StageTransform stageTransform, EnvironmentVariablesTransform variablesTransform, ParameterTransform parameterTransform) {
        this.materialTransform = materialTransform;
        this.stageTransform = stageTransform;
        this.variablesTransform = variablesTransform;
        this.parameterTransform = parameterTransform;
    }

    public JsonObject transform(Object maybePipe) {
        Map<String, Object> map = (Map<String, Object>) maybePipe;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            return transform(entry);
        }
        throw new RuntimeException("expected pipeline hash to have 1 item");
    }

    public JsonObject transform(Map.Entry<String, Object> entry) {
        String pipelineName = entry.getKey();
        JsonObject pipeline = new JsonObject();
        pipeline.addProperty(JSON_PIPELINE_NAME_FIELD, pipelineName);
        Map<String, Object> pipeMap = (Map<String, Object>) entry.getValue();

        addOptionalString(pipeline, pipeMap, JSON_PIPELINE_GROUP_FIELD, YAML_PIPELINE_GROUP_FIELD);
        addOptionalInteger(pipeline, pipeMap, JSON_PIPELINE_DISPLAY_ORDER_FIELD, YAML_PIPELINE_DISPLAY_ORDER_FIELD);
        addOptionalString(pipeline, pipeMap, JSON_PIPELINE_TEMPLATE_FIELD, YAML_PIPELINE_TEMPLATE_FIELD);
        addOptionalString(pipeline, pipeMap, JSON_PIPELINE_LABEL_TEMPLATE_FIELD, YAML_PIPELINE_LABEL_TEMPLATE_FIELD);
        addOptionalBoolean(pipeline, pipeMap, JSON_PIPELINE_PIPE_LOCKING_FIELD, YAML_PIPELINE_PIPE_LOCKING_FIELD);
        addOptionalString(pipeline, pipeMap, JSON_PIPELINE_LOCK_BEHAVIOR_FIELD, YAML_PIPELINE_LOCK_BEHAVIOR_FIELD);

        addOptionalObject(pipeline, pipeMap, JSON_PIPELINE_TRACKING_TOOL_FIELD, YAML_PIPELINE_TRACKING_TOOL_FIELD);
        addTimer(pipeline, pipeMap);

        JsonArray jsonEnvVariables = variablesTransform.transform(pipeMap);
        if (jsonEnvVariables != null && jsonEnvVariables.size() > 0)
            pipeline.add(JSON_ENV_VAR_FIELD, jsonEnvVariables);

        addMaterials(pipeline, pipeMap);
        if (!pipeline.has(JSON_PIPELINE_TEMPLATE_FIELD)) {
            addStages(pipeline, pipeMap);
        }

        JsonArray params = parameterTransform.transform(pipeMap);
        if (params != null && params.size() > 0) {
            pipeline.add(YAML_PIPELINE_PARAMETERS_FIELD, params);
        }

        return pipeline;
    }

    public Map<String, Object> inverseTransform(Object pipeline) {
        return inverseTransform((Map<String, Object>) pipeline);
    }

    public Map<String, Object> inverseTransform(Map<String, Object> pipeline) {
        Map<String, Object> result = new LinkedTreeMap<>();
        Map<String, Object> pipelineMap = new LinkedTreeMap<>();
        String name = (String) pipeline.get(JSON_PIPELINE_NAME_FIELD);

        pipelineMap.put(YAML_PIPELINE_GROUP_FIELD, pipeline.get(JSON_PIPELINE_GROUP_FIELD));
        addOptionalValue(pipelineMap, pipeline, JSON_PIPELINE_TEMPLATE_FIELD, YAML_PIPELINE_TEMPLATE_FIELD);
        addOptionalValue(pipelineMap, pipeline, JSON_PIPELINE_LABEL_TEMPLATE_FIELD, YAML_PIPELINE_LABEL_TEMPLATE_FIELD);
        addOptionalValue(pipelineMap, pipeline, JSON_PIPELINE_PIPE_LOCKING_FIELD, YAML_PIPELINE_PIPE_LOCKING_FIELD);
        addOptionalValue(pipelineMap, pipeline, JSON_PIPELINE_LOCK_BEHAVIOR_FIELD, YAML_PIPELINE_LOCK_BEHAVIOR_FIELD);
        addOptionalValue(pipelineMap, pipeline, JSON_PIPELINE_TRACKING_TOOL_FIELD, YAML_PIPELINE_TRACKING_TOOL_FIELD);
        addOptionalInt(pipelineMap, pipeline, JSON_PIPELINE_DISPLAY_ORDER_FIELD, YAML_PIPELINE_DISPLAY_ORDER_FIELD);

        addInverseTimer(pipelineMap, pipeline);

        Map<String, Object> yamlEnvVariables = variablesTransform.inverseTransform((List<Map<String, Object>>) pipeline.get(JSON_ENV_VAR_FIELD));
        if (yamlEnvVariables != null && yamlEnvVariables.size() > 0)
            pipelineMap.putAll(yamlEnvVariables);

        addInverseMaterials(pipelineMap, (List<Map<String, Object>>) pipeline.get(JSON_PIPELINE_MATERIALS_FIELD));
        if (!pipelineMap.containsKey(YAML_PIPELINE_TEMPLATE_FIELD)) {
            addInverseStages(pipelineMap, (List<Map<String, Object>>) pipeline.get(JSON_PIPELINE_STAGES_FIELD));
        }

        Map<String, Object> params = parameterTransform.inverseTransform((List<Map<String, Object>>) pipeline.get("parameters"));
        if (params != null && !params.isEmpty()) {
            pipelineMap.putAll(params);
        }

        result.put(name, pipelineMap);
        return result;

    }

    private void addInverseMaterials(Map<String, Object> pipelineMap, List<Map<String, Object>> materials) {
        Map<String, Object> inverseMaterials = new LinkedTreeMap<>();
        for (Map<String, Object> material : materials) {
            inverseMaterials.putAll(materialTransform.inverseTransform(material));
        }
        pipelineMap.put(YAML_PIPELINE_MATERIALS_FIELD, inverseMaterials);
    }

    private void addInverseStages(Map<String, Object> pipelineMap, List<Map<String, Object>> stages) {
        List<Map<String, Object>> inverseStages = new ArrayList<>();
        for (Map<String, Object> stage : stages) {
            inverseStages.add(null == stage ? null : stageTransform.inverseTransform(stage));
        }
        pipelineMap.put(YAML_PIPELINE_STAGES_FIELD, inverseStages);
    }

    private void addInverseTimer(Map<String, Object> pipelineMap, Map<String, Object> pipeline) {
        Object timer = pipeline.get(JSON_PIPELINE_TIMER_FIELD);
        if (timer == null)
            return;
        Map<String, Object> timerMap = (Map<String, Object>) timer;
        pipelineMap.put(YAML_PIPELINE_TIMER_FIELD, timerMap);
    }

    private void addTimer(JsonObject pipeline, Map<String, Object> pipeMap) {
        Object timer = pipeMap.get(YAML_PIPELINE_TIMER_FIELD);
        if (timer == null)
            return;
        JsonObject timerJson = new JsonObject();
        Map<String, Object> timerMap = (Map<String, Object>) timer;
        addRequiredString(timerJson, timerMap, "spec", "spec");
        addOptionalBoolean(timerJson, timerMap, "only_on_changes", "only_on_changes");
        pipeline.add(JSON_PIPELINE_TIMER_FIELD, timerJson);
    }

    private void addStages(JsonObject pipeline, Map<String, Object> pipeMap) {
        Object stages = pipeMap.get(YAML_PIPELINE_STAGES_FIELD);
        if (!(stages instanceof List))
            throw new YamlConfigException("expected a list of pipeline stages or a template reference");
        List<Object> stagesList = (List<Object>) stages;
        JsonArray stagesArray = transformStages(stagesList);
        pipeline.add(JSON_PIPELINE_STAGES_FIELD, stagesArray);
    }

    private JsonArray transformStages(List<Object> stagesList) {
        JsonArray stagesArray = new JsonArray();
        for (Object stage : stagesList) {
            if (stage instanceof List) {
                List<Object> stageNestedList = (List<Object>) stage;
                for(Object s : stageNestedList) {
                    stagesArray.add(stageTransform.transform(s));
                }
            }
            else
                stagesArray.add(stageTransform.transform(stage));
        }
        return stagesArray;
    }

    private void addMaterials(JsonObject pipeline, Map<String, Object> pipeMap) {
        Object materials = pipeMap.get(YAML_PIPELINE_MATERIALS_FIELD);
        if (!(materials instanceof Map))
            throw new YamlConfigException("expected a hash of pipeline materials");
        Map<String, Object> materialsMap = (Map<String, Object>) materials;
        JsonArray materialsArray = transformMaterials(materialsMap);
        pipeline.add(JSON_PIPELINE_MATERIALS_FIELD, materialsArray);
    }

    private JsonArray transformMaterials(Map<String, Object> materialsMap) {
        JsonArray materialsArray = new JsonArray();
        for (Map.Entry<String, Object> entry : materialsMap.entrySet()) {
            materialsArray.add(materialTransform.transform(entry));
        }
        return materialsArray;
    }
}
