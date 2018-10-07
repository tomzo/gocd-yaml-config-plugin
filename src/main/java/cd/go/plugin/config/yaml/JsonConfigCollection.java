package cd.go.plugin.config.yaml;

import com.google.gson.*;

import java.util.HashSet;
import java.util.Set;

public class JsonConfigCollection {
    private static final int DEFAULT_VERSION = 1;
    private final Gson gson;

    private Set<Integer> uniqueVersions = new HashSet<Integer>();
    private JsonObject mainObject = new JsonObject();
    private JsonArray environments = new JsonArray();
    private JsonArray pipelines = new JsonArray();
    private JsonArray templates = new JsonArray();
    private JsonArray errors = new JsonArray();

    public JsonConfigCollection() {
        gson = new Gson();

        updateTargetVersionTo(DEFAULT_VERSION);
        mainObject.add("environments", environments);
        mainObject.add("pipelines", pipelines);
        mainObject.add("templates", templates);
        mainObject.add("errors", errors);
    }

    protected JsonArray getEnvironments() {
        return environments;
    }

    public void addEnvironment(JsonElement environment, String location) {
        environments.add(environment);
        environment.getAsJsonObject().add("location", new JsonPrimitive(location));
    }

    public JsonObject getJsonObject() {
        return mainObject;
    }

    public void addPipeline(JsonElement pipeline, String location) {
        pipelines.add(pipeline);
        pipeline.getAsJsonObject().add("location", new JsonPrimitive(location));
    }

    public JsonArray getPipelines() {
        return pipelines;
    }

    public void addTemplate(JsonElement template, String location) {
        templates.add(template);
        template.getAsJsonObject().add("location", new JsonPrimitive(location));
    }

    public JsonArray getTemplates() {
        return templates;
    }

    public JsonArray getErrors() {
        return errors;
    }

    public void addError(String message, String location) {
        this.addError(new PluginError(message, location));
    }

    public void addError(PluginError error) {
        errors.add(gson.toJsonTree(error));
    }

    public void append(JsonConfigCollection other) {
        this.environments.addAll(other.environments);
        this.pipelines.addAll(other.pipelines);
        this.templates.addAll(other.templates);
        this.errors.addAll(other.errors);
        this.uniqueVersions.addAll(other.uniqueVersions);
    }

    public void updateFormatVersionFound(int version) {
        uniqueVersions.add(version);
        updateTargetVersionTo(version);
    }

    public void updateTargetVersionFromFiles() {
        if (uniqueVersions.size() > 1) {
            throw new RuntimeException("Versions across files are not unique. Found versions: " + uniqueVersions + ". There can only be one version across the whole repository.");
        }
        updateTargetVersionTo(uniqueVersions.iterator().hasNext() ? uniqueVersions.iterator().next() : DEFAULT_VERSION);
    }

    private void updateTargetVersionTo(int targetVersion) {
        mainObject.remove("target_version");
        mainObject.add("target_version", new JsonPrimitive(targetVersion));
    }
}