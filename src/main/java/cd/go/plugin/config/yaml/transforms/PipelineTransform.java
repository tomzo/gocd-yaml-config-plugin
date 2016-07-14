package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.YamlConfigException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import static cd.go.plugin.config.yaml.YamlUtils.*;
import static cd.go.plugin.config.yaml.transforms.EnvironmentVariablesTransform.JSON_ENV_VAR_FIELD;

public class PipelineTransform {
    private static final String JSON_PIPELINE_NAME_FIELD = "name";
    private static final String JSON_PIPELINE_GROUP_FIELD = "group";
    private static final String JSON_PIPELINE_LABEL_TEMPLATE_FIELD = "label_template";
    private static final String JSON_PIPELINE_PIPE_LOCKING_FIELD = "enable_pipeline_locking";
    private static final String JSON_PIPELINE_MINGLE_FIELD = "mingle";
    private static final String JSON_PIPELINE_TRACKING_TOOL_FIELD = "tracking_tool";
    private static final String JSON_PIPELINE_TIMER_FIELD = "timer";
    private static final String JSON_PIPELINE_MATERIALS_FIELD = "materials";
    private static final String JSON_PIPELINE_STAGES_FIELD = "stages";

    private static final String YAML_PIPELINE_GROUP_FIELD = "group";
    private static final String YAML_PIPELINE_LABEL_TEMPLATE_FIELD = "label_template";
    private static final String YAML_PIPELINE_PIPE_LOCKING_FIELD = "locking";
    private static final String YAML_PIPELINE_MINGLE_FIELD = "mingle";
    private static final String YAML_PIPELINE_TRACKING_TOOL_FIELD = "tracking_tool";
    private static final String YAML_PIPELINE_TIMER_FIELD = "timer";
    private static final String YAML_PIPELINE_MATERIALS_FIELD = "materials";
    private static final String YAML_PIPELINE_STAGES_FIELD = "stages";

    private final MaterialTransform materialTransform;
    private final StageTransform stageTransform;
    private final EnvironmentVariablesTransform variablesTransform;

    public PipelineTransform(MaterialTransform materialTransform,StageTransform stageTransform, EnvironmentVariablesTransform variablesTransform){
        this.materialTransform = materialTransform;
        this.stageTransform = stageTransform;
        this.variablesTransform = variablesTransform;
    }

    public JsonObject transform(Object maybePipe) {
        Map<String,Object> map = (Map<String,Object>)maybePipe;
        for(Map.Entry<String, Object> entry : map.entrySet()) {
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
        addOptionalString(pipeline, pipeMap, JSON_PIPELINE_LABEL_TEMPLATE_FIELD, YAML_PIPELINE_LABEL_TEMPLATE_FIELD);
        addOptionalBoolean(pipeline, pipeMap, JSON_PIPELINE_PIPE_LOCKING_FIELD, YAML_PIPELINE_PIPE_LOCKING_FIELD);

        addOptionalObject(pipeline, pipeMap, JSON_PIPELINE_TRACKING_TOOL_FIELD, YAML_PIPELINE_TRACKING_TOOL_FIELD);
        addOptionalObject(pipeline, pipeMap, JSON_PIPELINE_MINGLE_FIELD, YAML_PIPELINE_MINGLE_FIELD);
        addTimer(pipeline, pipeMap);

        JsonArray jsonEnvVariables = variablesTransform.transform(pipeMap);
        pipeline.add(JSON_ENV_VAR_FIELD,jsonEnvVariables);

        addMaterials(pipeline, pipeMap);
        addStages(pipeline, pipeMap);

        return pipeline;
    }

    private void addTimer(JsonObject pipeline, Map<String, Object> pipeMap) {
        Object timer = pipeMap.get(YAML_PIPELINE_TIMER_FIELD);
        if(timer == null)
            return;
        JsonObject timerJson = new JsonObject();
        Map<String, Object> timerMap = (Map<String, Object>)timer;
        addRequiredString(timerJson,timerMap,"spec","spec");
        addOptionalBoolean(timerJson,timerMap,"only_on_changes","only_on_changes");
        pipeline.add(JSON_PIPELINE_TIMER_FIELD,timerJson);
    }

    private void addStages(JsonObject pipeline, Map<String, Object> pipeMap) {
        Object stages = pipeMap.get(YAML_PIPELINE_STAGES_FIELD);
        if(stages == null || !(stages instanceof List))
            throw new YamlConfigException("expected a list of pipeline stages");
        List<Object> stagesList = (List<Object>)stages;
        JsonArray stagesArray = transformStages(stagesList);
        pipeline.add(JSON_PIPELINE_STAGES_FIELD,stagesArray);
    }

    private JsonArray transformStages(List<Object> stagesList) {
        JsonArray stagesArray = new JsonArray();
        for(Object stage : stagesList){
            stagesArray.add(stageTransform.transform(stage));
        }
        return stagesArray;
    }

    private void addMaterials(JsonObject pipeline, Map<String, Object> pipeMap) {
        Object materials = pipeMap.get(YAML_PIPELINE_MATERIALS_FIELD);
        if(materials == null || !(materials instanceof Map))
            throw new YamlConfigException("expected a hash of pipeline materials");
        Map<String,Object> materialsMap = (Map<String,Object>)materials;
        JsonArray materialsArray = transformMaterials(materialsMap);
        pipeline.add(JSON_PIPELINE_MATERIALS_FIELD,materialsArray);
    }

    private JsonArray transformMaterials(Map<String, Object> materialsMap) {
        JsonArray materialsArray = new JsonArray();
        for(Map.Entry<String, Object> entry : materialsMap.entrySet()) {
            materialsArray.add(materialTransform.transform(entry));
        }
        return materialsArray;
    }
}
