package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.JsonConfigCollection;
import cd.go.plugin.config.yaml.PluginError;
import cd.go.plugin.config.yaml.YamlConfigException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class RootTransform {
    private PipelineTransform pipelineTransform;
    private EnvironmentsTransform environmentsTransform;

    public RootTransform(PipelineTransform pipelineTransform,EnvironmentsTransform environmentsTransform) {
        this.pipelineTransform = pipelineTransform;
        this.environmentsTransform = environmentsTransform;
    }

    public JsonConfigCollection transform(Object rootObj,String location) {
        JsonConfigCollection partialConfig = new JsonConfigCollection();
        Map<String,Object> rootMap = (Map<String,Object>)rootObj;
        for(Map.Entry<String,Object> pe : rootMap.entrySet())
        {
            if("pipelines".equalsIgnoreCase(pe.getKey()))
            {
                if("".equals(pe.getValue()))
                    continue;
                Map<String,Object> pipelines = (Map<String,Object>)pe.getValue();
                for(Map.Entry<String,Object> pipe : pipelines.entrySet()){
                    try {
                        JsonElement jsonPipeline = pipelineTransform.transform(pipe);
                        partialConfig.addPipeline(jsonPipeline, location);
                    }
                    catch (Exception ex) {
                        partialConfig.addError(new PluginError(
                                String.format("Failed to parse pipeline %s; %s",pipe.getKey(),ex.getMessage()),location));
                    }
                }
            }
            else if("environments".equalsIgnoreCase(pe.getKey()))
            {
                if("".equals(pe.getValue()))
                    continue;
                Map<String,Object> environments = (Map<String,Object>)pe.getValue();
                for(Map.Entry<String,Object> env : environments.entrySet()){
                    try {
                        JsonElement jsonEnvironment = environmentsTransform.transform(env);
                        partialConfig.addEnvironment(jsonEnvironment, location);
                    }
                    catch (Exception ex) {
                        partialConfig.addError(new PluginError(
                                String.format("Failed to parse environment %s; %s",env.getKey(),ex.getMessage()),location));
                    }
                }
            }
            else
                throw new YamlConfigException(pe.getKey() + " is invalid, expected pipelines or environments");
        }
        return partialConfig;
    }
}
