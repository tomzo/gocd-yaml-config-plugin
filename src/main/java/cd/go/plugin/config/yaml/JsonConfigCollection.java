package cd.go.plugin.config.yaml;

import com.google.gson.*;

public class JsonConfigCollection {
    private static final int TARGET_VERSION = 1;
    private final Gson gson;

    private JsonObject mainObject = new JsonObject();
    private JsonArray environments = new JsonArray();
    private JsonArray pipelines = new JsonArray();
    private JsonArray errors = new JsonArray();

    public JsonConfigCollection()
    {
        gson = new Gson();

        mainObject.add("target_version",new JsonPrimitive(TARGET_VERSION));
        mainObject.add("environments",environments);
        mainObject.add("pipelines",pipelines);
        mainObject.add("errors",errors);
    }

    protected JsonArray getEnvironments()
    {
        return environments;
    }

    public void addEnvironment(JsonElement environment,String location) {
        environments.add(environment);
        environment.getAsJsonObject().add("location",new JsonPrimitive(location));
    }

    public JsonObject getJsonObject()
    {
        return mainObject;
    }

    public void addPipeline(JsonElement pipeline,String location) {
        pipelines.add(pipeline);
        pipeline.getAsJsonObject().add("location",new JsonPrimitive(location));
    }

    public JsonArray getPipelines() {
        return pipelines;
    }

    public JsonArray getErrors() {
        return errors;
    }

    public void addError(String message,String location) {
        this.addError(new PluginError(message,location));
    }

    public void addError(PluginError error) {
        errors.add(gson.toJsonTree(error));
    }

    public void append(JsonConfigCollection other) {
        this.environments.addAll(other.environments);
        this.pipelines.addAll(other.pipelines);
        this.errors.addAll(other.errors);
    }
}