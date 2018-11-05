package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.YamlConfigException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cd.go.plugin.config.yaml.JSONUtils.addOptionalInt;
import static cd.go.plugin.config.yaml.JSONUtils.addOptionalValue;
import static cd.go.plugin.config.yaml.YamlUtils.*;
import static cd.go.plugin.config.yaml.transforms.EnvironmentVariablesTransform.JSON_ENV_VAR_FIELD;

public class JobTransform extends ConfigurationTransform {
    private static final String JSON_JOB_NAME_FIELD = "name";
    public static final String YAML_JOB_TIMEOUT_FIELD = "timeout";
    public static final String JSON_JOB_TIMEOUT_FIELD = "timeout";
    public static final String YAML_JOB_TASKS_FIELD = "tasks";
    public static final String JSON_JOB_TASKS_FIELD = "tasks";
    public static final String YAML_JOB_RUN_INSTANCES_FIELD = "run_instances";
    public static final String JSON_JOB_RUN_INSTANCES_FIELD = "run_instance_count";
    private static final String YAML_JOB_TABS_FIELD = "tabs";
    private static final String JSON_JOB_TAB_NAME_FIELD = "name";
    private static final String JSON_JOB_TAB_PATH_FIELD = "path";
    private static final String JSON_JOB_TABS_FIELD = "tabs";
    private static final String JSON_JOB_RESOURCES_FIELD = "resources";
    private static final String YAML_JOB_RESOURCES_FIELD = "resources";
    private static final String JSON_JOB_ELASTIC_PROFILE_FIELD = "elastic_profile_id";
    private static final String YAML_JOB_ELASTIC_PROFILE_FIELD = "elastic_profile_id";
    private static final String YAML_JOB_ARTIFACTS_FIELD = "artifacts";
    private static final String JSON_JOB_ARTIFACTS_FIELD = "artifacts";
    private static final String JSON_JOB_ARTIFACT_SOURCE_FIELD = "source";
    private static final String YAML_JOB_ARTIFACT_SOURCE_FIELD = "source";
    private static final String JSON_JOB_ARTIFACT_DEST_FIELD = "destination";
    private static final String YAML_JOB_ARTIFACT_DEST_FIELD = "destination";
    private static final String JSON_JOB_ARTIFACT_ARTIFACT_ID_FIELD = "id";
    private static final String YAML_JOB_ARTIFACT_ARTIFACT_ID_FIELD = "id";
    private static final String JSON_JOB_ARTIFACT_STORE_ID_FIELD = "store_id";
    private static final String YAML_JOB_ARTIFACT_STORE_ID_FIELD = "store_id";

    private static final String YAML_JOB_PROPS_FIELD = "properties";
    private static final String JSON_JOB_PROPS_FIELD = "properties";
    private static final String JSON_JOB_PROP_NAME_FIELD = "name";
    private static final String JSON_JOB_PROP_SOURCE_FIELD = "source";
    private static final String YAML_JOB_PROP_SOURCE_FIELD = "source";
    private static final String JSON_JOB_PROP_XPATH_FIELD = "xpath";
    private static final String YAML_JOB_PROP_XPATH_FIELD = "xpath";

    private EnvironmentVariablesTransform environmentTransform;
    private TaskTransform taskTransform;

    public JobTransform(EnvironmentVariablesTransform environmentTransform, TaskTransform taskTransform) {
        this.environmentTransform = environmentTransform;
        this.taskTransform = taskTransform;
    }

    public JsonObject transform(Object yamlObject) {
        Map<String, Object> map = (Map<String, Object>) yamlObject;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            return transform(entry);
        }
        throw new RuntimeException("expected job hash to have 1 item");
    }

    public JsonObject transform(Map.Entry<String, Object> entry) {
        return transform(entry.getKey(), (Map<String, Object>) entry.getValue());
    }

    public JsonObject transform(String jobName, Map<String, Object> jobMap) {
        JsonObject jobJson = new JsonObject();
        jobJson.addProperty(JSON_JOB_NAME_FIELD, jobName);
        addOptionalInteger(jobJson, jobMap, JSON_JOB_TIMEOUT_FIELD, YAML_JOB_TIMEOUT_FIELD);
        addRunInstances(jobMap, jobJson);
        JsonArray jsonEnvVariables = environmentTransform.transform(jobMap);
        if (jsonEnvVariables != null && jsonEnvVariables.size() > 0)
            jobJson.add(JSON_ENV_VAR_FIELD, jsonEnvVariables);
        addTabs(jobJson, jobMap);
        addOptionalStringList(jobJson, jobMap, JSON_JOB_RESOURCES_FIELD, YAML_JOB_RESOURCES_FIELD);
        addOptionalString(jobJson, jobMap, JSON_JOB_ELASTIC_PROFILE_FIELD, YAML_JOB_ELASTIC_PROFILE_FIELD);
        addArtifacts(jobJson, jobMap);
        addProperties(jobJson, jobMap);
        addTasks(jobJson, jobMap);
        return jobJson;
    }

    public LinkedTreeMap<String, Object> inverseTransform(LinkedTreeMap<String, Object> job) {
        if (job == null)
            return null;
        String jobName = (String) job.get(JSON_JOB_NAME_FIELD);
        LinkedTreeMap<String, Object> inverseJob = new LinkedTreeMap<>();
        LinkedTreeMap<String, Object> jobData = new LinkedTreeMap<>();

        addOptionalInt(jobData, job, JSON_JOB_TIMEOUT_FIELD, YAML_JOB_TIMEOUT_FIELD);

        addInverseRunInstances(jobData, job);

        LinkedTreeMap<String, Object> yamlEnvVariables = environmentTransform.inverseTransform((List<LinkedTreeMap<String, Object>>) job.get(JSON_ENV_VAR_FIELD));
        if (yamlEnvVariables != null && yamlEnvVariables.size() > 0)
            jobData.putAll(yamlEnvVariables);

        addInverseTabs(jobData, job);

        addOptionalValue(jobData, job, JSON_JOB_RESOURCES_FIELD, YAML_JOB_RESOURCES_FIELD);
        addOptionalValue(jobData, job, JSON_JOB_ELASTIC_PROFILE_FIELD, YAML_JOB_ELASTIC_PROFILE_FIELD);

        addInverseArtifacts(jobData, job);
        addInverseProperties(jobData, job);
        addInverseTasks(jobData, job);
        inverseJob.put(jobName, jobData);
        return inverseJob;
    }

    private void addInverseRunInstances(LinkedTreeMap<String, Object> jobData, LinkedTreeMap<String, Object> job) {
        Object run = job.get(JSON_JOB_RUN_INSTANCES_FIELD);
        if (run == null)
            return;
        if (run instanceof String) {
            addOptionalValue(jobData, job, JSON_JOB_RUN_INSTANCES_FIELD, YAML_JOB_RUN_INSTANCES_FIELD);
        } else {
            addOptionalInt(jobData, job, JSON_JOB_RUN_INSTANCES_FIELD, YAML_JOB_RUN_INSTANCES_FIELD);
        }
    }

    private void addInverseTabs(LinkedTreeMap<String, Object> jobData, LinkedTreeMap<String, Object> job) {
        List<LinkedTreeMap<String, Object>> tabs = (List<LinkedTreeMap<String, Object>>) job.get(JSON_JOB_TABS_FIELD);
        if (tabs == null)
            return;

        LinkedTreeMap<String, Object> inverseTabs = new LinkedTreeMap<>();
        for (LinkedTreeMap<String, Object> tab : tabs) {
           inverseTabs.put((String) tab.get(JSON_JOB_TAB_NAME_FIELD), tab.get(JSON_JOB_TAB_PATH_FIELD)) ;
        }

        jobData.put(YAML_JOB_TABS_FIELD, inverseTabs);
    }

    private void addInverseTasks(LinkedTreeMap<String, Object> jobData, LinkedTreeMap<String, Object> job) {
        List<LinkedTreeMap<String, Object>> tasks = (List<LinkedTreeMap<String, Object>>) job.get(JSON_JOB_TASKS_FIELD);
        if (tasks == null)
            return;

        List<LinkedTreeMap<String, Object>> inverseTasks = new ArrayList<>();

        for (LinkedTreeMap<String, Object> task : tasks) {
           inverseTasks.add(taskTransform.inverseTransform(task));
        }

        jobData.put(YAML_JOB_TASKS_FIELD, inverseTasks);
    }

    private void addInverseProperties(LinkedTreeMap<String, Object> jobData, LinkedTreeMap<String, Object> job) {
        List<LinkedTreeMap<String, Object>> properties = (List<LinkedTreeMap<String, Object>>) job.get(JSON_JOB_PROPS_FIELD);
        if(properties == null)
            return;

        LinkedTreeMap<String, Object> inverseProperties = new LinkedTreeMap<>();

        for (LinkedTreeMap<String, Object> prop : properties) {
            String name = (String) prop.remove(JSON_JOB_PROP_NAME_FIELD);
            inverseProperties.put(name, prop);
        }

        jobData.put(YAML_JOB_PROPS_FIELD, inverseProperties);
    }

    private void addProperties(JsonObject jobJson, Map<String, Object> jobMap) {
        Object props = jobMap.get(YAML_JOB_PROPS_FIELD);
        if (props == null)
            return;
        if (!(props instanceof Map))
            throw new YamlConfigException("properties should be a hash");
        JsonArray propsJson = new JsonArray();
        Map<String, Object> propsMap = (Map<String, Object>) props;
        for (Map.Entry<String, Object> propEntry : propsMap.entrySet()) {
            String propName = propEntry.getKey();
            Object propObj = propEntry.getValue();
            if (!(propObj instanceof Map))
                throw new YamlConfigException("property " + propName + " should be a hash");
            Map<String, Object> propMap = (Map<String, Object>) propObj;
            JsonObject propJson = new JsonObject();
            propJson.addProperty(JSON_JOB_PROP_NAME_FIELD, propName);
            addRequiredString(propJson, propMap, JSON_JOB_PROP_SOURCE_FIELD, YAML_JOB_PROP_SOURCE_FIELD);
            addRequiredString(propJson, propMap, JSON_JOB_PROP_XPATH_FIELD, YAML_JOB_PROP_XPATH_FIELD);
            propsJson.add(propJson);
        }
        jobJson.add(JSON_JOB_PROPS_FIELD, propsJson);
    }

    private void addInverseArtifacts(LinkedTreeMap<String, Object> jobData, LinkedTreeMap<String, Object> job) {
        List<LinkedTreeMap<String, Object>> artifacts = (List<LinkedTreeMap<String, Object>>) job.get(JSON_JOB_ARTIFACTS_FIELD);
        if (artifacts == null)
            return;

        List<LinkedTreeMap<String, Object>> inverseArtifacts = new ArrayList<>();
        for (LinkedTreeMap<String, Object> artifact : artifacts) {
            LinkedTreeMap<String, Object> inverseArtifact = new LinkedTreeMap<>();

            String type = (String) artifact.remove("type");
            inverseArtifact.put(type, artifact);
            inverseArtifacts.add(inverseArtifact);
        }

        jobData.put(YAML_JOB_ARTIFACTS_FIELD, inverseArtifacts);
    }

    private void addArtifacts(JsonObject jobJson, Map<String, Object> jobMap) {
        Object artifacts = jobMap.get(YAML_JOB_ARTIFACTS_FIELD);
        if (artifacts == null)
            return;
        if (!(artifacts instanceof List))
            throw new YamlConfigException("artifacts should be a list of hashes");
        JsonArray artifactArrayJson = new JsonArray();
        List<Object> artifactsList = (List<Object>) artifacts;
        for (Object artifactObj : artifactsList) {
            if (!(artifactObj instanceof Map))
                throw new YamlConfigException("artifact should be a hash - build:, test: or external:");

            Map<String, Object> artifactMap = (Map<String, Object>) artifactObj;
            for (Map.Entry<String, Object> artMap : artifactMap.entrySet()) {
                JsonObject artifactJson = new JsonObject();
                if ("build".equalsIgnoreCase(artMap.getKey()))
                    artifactJson.addProperty("type", "build");
                else if ("test".equalsIgnoreCase(artMap.getKey()))
                    artifactJson.addProperty("type", "test");
                else if ("external".equalsIgnoreCase(artMap.getKey())) {
                    artifactJson.addProperty("type", "external");
                } else
                    throw new YamlConfigException("expected build:, test:, or external: in artifact, got " + artMap.getKey());

                Map<String, Object> artMapValue = (Map<String, Object>) artMap.getValue();
                if ("external".equalsIgnoreCase(artMap.getKey())) {
                    addRequiredString(artifactJson, artMapValue, JSON_JOB_ARTIFACT_ARTIFACT_ID_FIELD, YAML_JOB_ARTIFACT_ARTIFACT_ID_FIELD );
                    addRequiredString(artifactJson, artMapValue, JSON_JOB_ARTIFACT_STORE_ID_FIELD, YAML_JOB_ARTIFACT_STORE_ID_FIELD );
                    super.addConfiguration(artifactJson, (Map<String, Object>) artMapValue.get("configuration"));
                } else {
                    addRequiredString(artifactJson, artMapValue, JSON_JOB_ARTIFACT_SOURCE_FIELD, YAML_JOB_ARTIFACT_SOURCE_FIELD);
                    addOptionalString(artifactJson, artMapValue, JSON_JOB_ARTIFACT_DEST_FIELD, YAML_JOB_ARTIFACT_DEST_FIELD);
                }
                artifactArrayJson.add(artifactJson);
                break;// we read first hash and exit
            }
        }
        jobJson.add(JSON_JOB_ARTIFACTS_FIELD, artifactArrayJson);
    }

    private void addTabs(JsonObject jobJson, Map<String, Object> jobMap) {
        Object tabs = jobMap.get(YAML_JOB_TABS_FIELD);
        if (tabs == null)
            return;
        if (!(tabs instanceof Map))
            throw new YamlConfigException("tabs should be a hash");
        JsonArray tabsJson = new JsonArray();
        Map<String, String> tabsMap = (Map<String, String>) tabs;
        for (Map.Entry<String, String> tab : tabsMap.entrySet()) {
            String tabName = tab.getKey();
            String tabPath = tab.getValue();
            JsonObject tabJson = new JsonObject();
            tabJson.addProperty(JSON_JOB_TAB_NAME_FIELD, tabName);
            tabJson.addProperty(JSON_JOB_TAB_PATH_FIELD, tabPath);
            tabsJson.add(tabJson);
        }
        jobJson.add(JSON_JOB_TABS_FIELD, tabsJson);
    }

    private void addRunInstances(Map<String, Object> jobMap, JsonObject jobJson) {
        String runInstancesText = getOptionalString(jobMap, YAML_JOB_RUN_INSTANCES_FIELD);
        if (runInstancesText != null) {
            if ("all".equalsIgnoreCase(runInstancesText))
                jobJson.addProperty(JSON_JOB_RUN_INSTANCES_FIELD, "all");
            else {
                try {
                    jobJson.addProperty(JSON_JOB_RUN_INSTANCES_FIELD, NumberFormat.getInstance().parse(runInstancesText));
                } catch (ParseException e) {
                    throw new YamlConfigException(YAML_JOB_RUN_INSTANCES_FIELD + " must be 'all' or a number", e);
                }
            }
        }
    }

    private void addTasks(JsonObject jobJson, Map<String, Object> jobMap) {
        Object tasksObj = jobMap.get(YAML_JOB_TASKS_FIELD);
        if (tasksObj == null)
            throw new YamlConfigException("tasks are required in a job");
        JsonArray tasksJson = new JsonArray();
        List<Object> taskList = (List<Object>) tasksObj;
        addTasks(taskList, tasksJson);
        jobJson.add(JSON_JOB_TASKS_FIELD, tasksJson);
    }

    private void addTasks(List<Object> taskList, JsonArray tasksJson) {
        for (Object maybeTask : taskList) {
            if (maybeTask instanceof List) {
                List<Object> taskNestedList = (List<Object>) maybeTask;
                addTasks(taskNestedList, tasksJson);
            } else {
                JsonObject task = taskTransform.transform(maybeTask);
                tasksJson.add(task);
            }
        }
    }

}
