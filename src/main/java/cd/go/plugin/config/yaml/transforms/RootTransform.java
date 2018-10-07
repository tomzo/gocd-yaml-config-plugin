package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.JsonConfigCollection;
import cd.go.plugin.config.yaml.PluginError;
import cd.go.plugin.config.yaml.YamlConfigException;
import com.google.gson.JsonElement;

import java.util.Map;

public class RootTransform {
    private PipelineTransform pipelineTransform;
    private EnvironmentsTransform environmentsTransform;
    private TemplateTransform templateTransform;

    public RootTransform() {
        EnvironmentVariablesTransform environmentVarsTransform = new EnvironmentVariablesTransform();
        MaterialTransform material = new MaterialTransform();
        ParameterTransform parameterTransform = new ParameterTransform();
        JobTransform job = new JobTransform(environmentVarsTransform, new TaskTransform());
        StageTransform stage = new StageTransform(environmentVarsTransform, job);
        this.pipelineTransform = new PipelineTransform(material, stage, environmentVarsTransform, parameterTransform);
        this.environmentsTransform = new EnvironmentsTransform(environmentVarsTransform);
        this.templateTransform = new TemplateTransform(stage);
    }

    public RootTransform(PipelineTransform pipelineTransform, EnvironmentsTransform environmentsTransform, TemplateTransform templateTransform) {
        this.pipelineTransform = pipelineTransform;
        this.environmentsTransform = environmentsTransform;
        this.templateTransform = templateTransform;
    }

    public JsonConfigCollection transform(Object rootObj, String location) {
        JsonConfigCollection partialConfig = new JsonConfigCollection();
        Map<String, Object> rootMap = (Map<String, Object>) rootObj;
        for (Map.Entry<String, Object> pe : rootMap.entrySet()) {
            if ("pipelines".equalsIgnoreCase(pe.getKey())) {
                if (pe.getValue() == null)
                    continue;
                Map<String, Object> pipelines = (Map<String, Object>) pe.getValue();
                for (Map.Entry<String, Object> pipe : pipelines.entrySet()) {
                    try {
                        JsonElement jsonPipeline = pipelineTransform.transform(pipe);
                        partialConfig.addPipeline(jsonPipeline, location);
                    } catch (Exception ex) {
                        partialConfig.addError(new PluginError(
                                String.format("Failed to parse pipeline %s; %s", pipe.getKey(), ex.getMessage()), location));
                    }
                }
            } else if ("templates".equalsIgnoreCase(pe.getKey())) {
                if (pe.getValue() == null)
                    continue;
                Map<String, Object> templates = (Map<String, Object>) pe.getValue();
                for (Map.Entry<String, Object> template : templates.entrySet()) {
                    try {
                        JsonElement jsonTemplate = templateTransform.transform(template);
                        partialConfig.addTemplate(jsonTemplate, location);
                    } catch (Exception ex) {
                        partialConfig.addError(new PluginError(
                                String.format("Failed to parse template %s; %s", template.getKey(), ex.getMessage()), location));
                    }
                }
            } else if ("environments".equalsIgnoreCase(pe.getKey())) {
                if (pe.getValue() == null)
                    continue;
                Map<String, Object> environments = (Map<String, Object>) pe.getValue();
                for (Map.Entry<String, Object> env : environments.entrySet()) {
                    try {
                        JsonElement jsonEnvironment = environmentsTransform.transform(env);
                        partialConfig.addEnvironment(jsonEnvironment, location);
                    } catch (Exception ex) {
                        partialConfig.addError(new PluginError(
                                String.format("Failed to parse environment %s; %s", env.getKey(), ex.getMessage()), location));
                    }
                }
            } else if ("format_version".equalsIgnoreCase(pe.getKey())) {
                partialConfig.updateFormatVersionFound(Integer.valueOf((String) pe.getValue()));
            } else if (!"common".equalsIgnoreCase(pe.getKey()))
                throw new YamlConfigException(pe.getKey() + " is invalid, expected format_version, pipelines, environments, or common");
        }
        return partialConfig;
    }
}
