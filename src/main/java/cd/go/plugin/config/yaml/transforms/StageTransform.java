package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.YamlConfigException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;
import java.util.Map;

import static cd.go.plugin.config.yaml.JSONUtils.*;
import static cd.go.plugin.config.yaml.YamlUtils.*;
import static cd.go.plugin.config.yaml.transforms.EnvironmentVariablesTransform.JSON_ENV_VAR_FIELD;

public class StageTransform {
    private static final String JSON_STAGE_NAME_FIELD = "name";
    private static final String JSON_STAGE_FETCH_FIELD = "fetch_materials";
    private static final String YAML_STAGE_FETCH_FIELD = "fetch_materials";
    private static final String JSON_STAGE_NEVER_CLEAN_FIELD = "never_cleanup_artifacts";
    private static final String YAML_STAGE_KEEP_ARTIFACTS_FIELD = "keep_artifacts";
    private static final String JSON_STAGE_CLEAN_WORK_FIELD = "clean_working_directory";
    private static final String YAML_STAGE_CLEAN_WORK_FIELD = "clean_workspace";
    private static final String YAML_STAGE_APPROVAL_FIELD = "approval";
    private static final String JSON_STAGE_APPROVAL_FIELD = "approval";
    private static final String JSON_STAGE_APPROVAL_TYPE_FIELD = "type";
    private static final String YAML_STAGE_APPROVAL_TYPE_FIELD = "type";
    private static final String JSON_STAGE_JOBS_FIELD = "jobs";
    private static final String JSON_STAGE_APPROVAL_ALLOW_ONLY_ON_SUCCESS_FIELD = "allow_only_on_success";
    private static final String YAML_STAGE_APPROVAL_ALLOW_ONLY_ON_SUCCESS_FIELD = "allow_only_on_success";
    private static final String JSON_STAGE_APPROVAL_USERS_FIELD = "users";
    private static final String YAML_STAGE_APPROVAL_USERS_FIELD = "users";
    private static final String JSON_STAGE_APPROVAL_ROLES_FIELD = "roles";
    private static final String YAML_STAGE_APPROVAL_ROLES_FIELD = "roles";

    private EnvironmentVariablesTransform environmentTransform;
    private JobTransform jobTransform;

    public StageTransform(EnvironmentVariablesTransform environmentTransform, JobTransform jobTransform) {
        this.environmentTransform = environmentTransform;
        this.jobTransform = jobTransform;
    }

    public JsonObject transform(Object maybeStage) {
        Map<String, Object> map = (Map<String, Object>) maybeStage;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            return transform(entry);
        }
        throw new RuntimeException("expected stage hash to have 1 item");
    }

    public JsonObject transform(Map.Entry<String, Object> entry) {
        String stageName = entry.getKey();
        JsonObject stage = new JsonObject();
        stage.addProperty(JSON_STAGE_NAME_FIELD, stageName);
        Map<String, Object> stageMap = (Map<String, Object>) entry.getValue();

        addOptionalBoolean(stage, stageMap, JSON_STAGE_FETCH_FIELD, YAML_STAGE_FETCH_FIELD);
        addOptionalBoolean(stage, stageMap, JSON_STAGE_NEVER_CLEAN_FIELD, YAML_STAGE_KEEP_ARTIFACTS_FIELD);
        addOptionalBoolean(stage, stageMap, JSON_STAGE_CLEAN_WORK_FIELD, YAML_STAGE_CLEAN_WORK_FIELD);

        addApproval(stage, stageMap);

        JsonArray jsonEnvVariables = environmentTransform.transform(stageMap);
        if (jsonEnvVariables != null && jsonEnvVariables.size() > 0)
            stage.add(JSON_ENV_VAR_FIELD, jsonEnvVariables);

        addJobs(stage, stageMap);

        return stage;
    }

    public Map<String, Object> inverseTransform(Map<String, Object> stage) {
        if (stage == null) {
            return null;
        }
        String stageName = (String) stage.get(JSON_STAGE_NAME_FIELD);
        Map<String, Object> inverseStage = new LinkedTreeMap<>();
        Map<String, Object> stageData = new LinkedTreeMap<>();

        addOptionalValue(stageData, stage, JSON_STAGE_FETCH_FIELD, YAML_STAGE_FETCH_FIELD);
        addOptionalValue(stageData, stage, JSON_STAGE_NEVER_CLEAN_FIELD, YAML_STAGE_KEEP_ARTIFACTS_FIELD);
        addOptionalValue(stageData, stage, JSON_STAGE_CLEAN_WORK_FIELD, YAML_STAGE_CLEAN_WORK_FIELD);
        addOptionalValue(stageData, stage, JSON_STAGE_APPROVAL_ALLOW_ONLY_ON_SUCCESS_FIELD, YAML_STAGE_APPROVAL_ALLOW_ONLY_ON_SUCCESS_FIELD);

        addInverseApproval(stageData, stage);

        Map<String, Object> yamlEnvVariables = environmentTransform.inverseTransform((List<Map<String, Object>>) stage.get(JSON_ENV_VAR_FIELD));
        if (yamlEnvVariables != null && yamlEnvVariables.size() > 0)
            stageData.putAll(yamlEnvVariables);

        addInverseJobs(stageData, (List<Map<String, Object>>) stage.get(JSON_STAGE_JOBS_FIELD));

        inverseStage.put(stageName, stageData);
        return inverseStage;
    }

    private void addInverseJobs(Map<String, Object> stageData, List<Map<String, Object>> jobs) {
        Map<String, Object> inverseJobs = new LinkedTreeMap<>();
        for (Map<String, Object> job : jobs) {
            Map<String, Object> inverseJob = jobTransform.inverseTransform(job);
            if (inverseJob != null)
                inverseJobs.putAll(inverseJob);
        }

        stageData.put("jobs", inverseJobs);
    }

    private void addInverseApproval(Map<String, Object> stageData, Map<String, Object> stage) {
        Map<String, Object> approval = (Map<String, Object>) stage.get(JSON_STAGE_APPROVAL_FIELD);
        Map<String, Object> inverseApproval = new LinkedTreeMap<>();
        if (approval == null)
            return;

        addRequiredValue(inverseApproval, approval, JSON_STAGE_APPROVAL_TYPE_FIELD, YAML_STAGE_APPROVAL_TYPE_FIELD);
        addOptionalValue(inverseApproval, approval, JSON_STAGE_APPROVAL_ALLOW_ONLY_ON_SUCCESS_FIELD, YAML_STAGE_APPROVAL_ALLOW_ONLY_ON_SUCCESS_FIELD);
        addOptionalList(inverseApproval, approval, JSON_STAGE_APPROVAL_ROLES_FIELD, YAML_STAGE_APPROVAL_ROLES_FIELD);
        addOptionalList(inverseApproval, approval, JSON_STAGE_APPROVAL_USERS_FIELD, YAML_STAGE_APPROVAL_USERS_FIELD);

        stageData.put(YAML_STAGE_APPROVAL_FIELD, inverseApproval);
    }

    private void addApproval(JsonObject stage, Map<String, Object> stageMap) {
        Object approval = stageMap.get(YAML_STAGE_APPROVAL_FIELD);
        if (approval == null)
            return;
        JsonObject approvalJson = new JsonObject();
        if (approval instanceof String) {
            // shorthand
            if ("auto".equals(approval) || "success".equals(approval))
                approvalJson.addProperty(JSON_STAGE_APPROVAL_TYPE_FIELD, "success");
            else if ("manual".equals(approval))
                approvalJson.addProperty(JSON_STAGE_APPROVAL_TYPE_FIELD, "manual");
            else
                throw new YamlConfigException("Approval should be a hash or string [auto or manual]");
        } else {
            Map<String, Object> approvalMap = (Map<String, Object>) approval;
            addRequiredString(approvalJson, approvalMap, JSON_STAGE_APPROVAL_TYPE_FIELD, YAML_STAGE_APPROVAL_TYPE_FIELD);
            addOptionalBoolean(approvalJson, approvalMap, JSON_STAGE_APPROVAL_ALLOW_ONLY_ON_SUCCESS_FIELD, YAML_STAGE_APPROVAL_ALLOW_ONLY_ON_SUCCESS_FIELD);
            addOptionalStringList(approvalJson, approvalMap, JSON_STAGE_APPROVAL_USERS_FIELD, YAML_STAGE_APPROVAL_USERS_FIELD);
            addOptionalStringList(approvalJson, approvalMap, JSON_STAGE_APPROVAL_ROLES_FIELD, YAML_STAGE_APPROVAL_ROLES_FIELD);
        }

        stage.add(JSON_STAGE_APPROVAL_FIELD, approvalJson);
    }

    private void addJobs(JsonObject stage, Map<String, Object> stageMap) {
        JsonArray jsonJobs = new JsonArray();
        Object jobsObj = stageMap.get("jobs");
        if (jobsObj == null) {
            //there are no jobs, then it might be a single-job stage
            Object tasks = stageMap.get("tasks");
            if (tasks == null)
                throw new YamlConfigException("Stage must have jobs defined, or tasks if only one job should exist");
            // single-job stage - expect to find job definition in the stage definition
            JsonObject job = jobTransform.transform(stage.get(JSON_STAGE_NAME_FIELD).getAsString(), stageMap);
            jsonJobs.add(job);
        } else {
            // standard definition
            Map<String, Object> jobsMap = (Map<String, Object>) jobsObj;
            for (Map.Entry<String, Object> entry : jobsMap.entrySet()) {
                JsonObject job = jobTransform.transform(entry);
                jsonJobs.add(job);
            }
        }
        stage.add(JSON_STAGE_JOBS_FIELD, jsonJobs);
    }
}
