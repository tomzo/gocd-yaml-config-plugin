package cd.go.plugin.config.yaml;

import cd.go.plugin.config.yaml.transforms.RootTransform;
import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static cd.go.plugin.config.yaml.YamlUtils.TYPE;
import static com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse.badRequest;

@Extension
public class YamlConfigPlugin implements GoPlugin {
    public static final String GET_PLUGIN_SETTINGS = "go.processor.plugin-settings.get";
    private static final String DISPLAY_NAME_FILE_PATTERN = "Go YAML files pattern";
    private static final String DISPLAY_NAME_GENERATOR_CONFIG_PATTERN = "Go YAML generator config files pattern";
    private static final String PLUGIN_SETTINGS_FILE_PATTERN = "file_pattern";
    private static final String PLUGIN_SETTINGS_GENERATOR_CONFIG_PATTERN = "generator_config_pattern";
    private static final String MISSING_DIRECTORY_MESSAGE = "directory property is missing in parse-directory request";
    private static final String EMPTY_REQUEST_BODY_MESSAGE = "Request body cannot be null or empty";
    private static final String PLUGIN_ID = "yaml.config.plugin";
    public static final String PLUGIN_SETTINGS_GET_CONFIGURATION = "go.plugin-settings.get-configuration";
    public static final String PLUGIN_SETTINGS_GET_VIEW = "go.plugin-settings.get-view";
    public static final String PLUGIN_SETTINGS_VALIDATE_CONFIGURATION = "go.plugin-settings.validate-configuration";
    public static final String DEFAULT_FILE_PATTERN = "**/*.gocd.yaml,**/*.gocd.yml";
    public static final String DEFAULT_GENERATOR_CONFIG_PATTERN = "**/*.gocd-yaml-generator.yaml,**/*.gocd-yaml-generator.yml";

    private static Logger LOGGER = Logger.getLoggerFor(YamlConfigPlugin.class);

    private final Gson gson = new Gson();
    private GoApplicationAccessor goApplicationAccessor;

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        this.goApplicationAccessor = goApplicationAccessor;
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return getGoPluginIdentifier();
    }

    private GoPluginIdentifier getGoPluginIdentifier() {
        return new GoPluginIdentifier("configrepo", Arrays.asList("1.0", "2.0"));
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
        String requestName = request.requestName();
        if (requestName.equals(PLUGIN_SETTINGS_GET_CONFIGURATION)) {
            return handleGetPluginSettingsConfiguration();
        } else if (requestName.equals(PLUGIN_SETTINGS_GET_VIEW)) {
            try {
                return handleGetPluginSettingsView();
            } catch (IOException e) {
                return renderJSON(500, String.format("Failed to find template: %s", e.getMessage()));
            }
        } else if (requestName.equals(PLUGIN_SETTINGS_VALIDATE_CONFIGURATION)) {
            return handleValidatePluginSettingsConfiguration(request);
        }
        if ("parse-directory".equals(request.requestName())) {
            return handleParseDirectoryRequest(request);
        } else if ("pipeline-export".equals(requestName)) {
            return handlePipelineExportRequest(request);
        } else if ("get-capabilities".equals(requestName)) {
            return DefaultGoPluginApiResponse.success(gson.toJson(new Capabilities()));
        }
        throw new UnhandledRequestTypeException(request.requestName());
    }

    private GoPluginApiResponse handlePipelineExportRequest(GoPluginApiRequest request) {
        Map<String, String> response = new HashMap<>();
        HashMap<String, LinkedTreeMap<String, Object>> parsedBody = gson.fromJson(request.requestBody(), TYPE);
        LinkedTreeMap<String, Object> pipeline = parsedBody.get("pipeline");
        response.put("pipeline", new RootTransform().inverseTransformPipeline(pipeline));
        return DefaultGoPluginApiResponse.success(gson.toJson(response));
    }

    private GoPluginApiResponse handleParseDirectoryRequest(GoPluginApiRequest request) {
        JsonParser jsonParser = new JsonParser();
        try {
            String requestBody = request.requestBody();
            if (requestBody == null) {
                return badRequest(EMPTY_REQUEST_BODY_MESSAGE);
            }
            JsonElement parsedResponse;
            try {
                parsedResponse = jsonParser.parse(requestBody);
            } catch (JsonParseException parseException) {
                return badRequest("Request body must be valid JSON string");
            }
            if (parsedResponse.equals(new JsonObject())) {
                return badRequest(EMPTY_REQUEST_BODY_MESSAGE);
            }
            JsonObject parsedResponseObject = parsedResponse.getAsJsonObject();
            JsonPrimitive directoryJsonPrimitive = parsedResponseObject.getAsJsonPrimitive("directory");
            if (directoryJsonPrimitive == null) {
                return badRequest(MISSING_DIRECTORY_MESSAGE);
            }
            String directory = directoryJsonPrimitive.getAsString();
            File baseDir = new File(directory);

            String pattern = null;
            String generatorConfigPattern = null;
            JsonArray perRepoConfig = parsedResponseObject.getAsJsonArray("configurations");
            if(perRepoConfig != null) {
                for(JsonElement config : perRepoConfig) {
                    JsonObject configObj = config.getAsJsonObject();
                    String key = configObj.getAsJsonPrimitive("key").getAsString();
                    if(key.equals(PLUGIN_SETTINGS_FILE_PATTERN)) {
                        pattern = configObj.getAsJsonPrimitive("value").getAsString();
                    }
                    else if(key.equals(PLUGIN_SETTINGS_GENERATOR_CONFIG_PATTERN)) {
                        generatorConfigPattern = configObj.getAsJsonPrimitive("value").getAsString();
                    }
                    else
                        return badRequest("Config repo configuration has invalid key=" + key);
                }
            }

            PluginSettings settings = getPluginSettings();
            ConfigDirectoryScanner scanner = new AntDirectoryScanner();

            if(generatorConfigPattern == null) {
                generatorConfigPattern = isBlank(settings.getGeneratorConfigPattern()) ?
                        DEFAULT_GENERATOR_CONFIG_PATTERN : settings.getGeneratorConfigPattern();
            }

            List<YamlGenerator> generators = new ArrayList<>();

            String[] generatorConfigFiles = scanner.getFilesMatchingPattern(baseDir, generatorConfigPattern);
            if (generatorConfigFiles.length > 0) {
                YamlGeneratorConfigParser parser = new YamlGeneratorConfigParser();
                for (String file : generatorConfigFiles) {
                    generators.addAll(parser.parseFile(baseDir, file));
                }
            }

            List<String> files = new ArrayList<>();

            for (YamlGenerator generator : generators) {
                files.addAll(generator.generateFiles(baseDir));
            }

            if(pattern == null) {
                pattern = isBlank(settings.getFilePattern()) ?
                        DEFAULT_FILE_PATTERN : settings.getFilePattern();
            }

            YamlFileParser parser = new YamlFileParser();
            for (String file : scanner.getFilesMatchingPattern(baseDir, pattern))
               files.add(file);
            JsonConfigCollection config = parser.parseFiles(baseDir, files);

            config.updateTargetVersionFromFiles();
            JsonObject responseJsonObject = config.getJsonObject();

            return DefaultGoPluginApiResponse.success(gson.toJson(responseJsonObject));
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred in YAML configuration plugin.", e);
            JsonConfigCollection config = new JsonConfigCollection();
            config.addError(new PluginError(e.toString(), "YAML config plugin"));
            return DefaultGoPluginApiResponse.error(gson.toJson(config.getJsonObject()));
        }
    }

    private boolean isBlank(String pattern) {
        return pattern == null || pattern.isEmpty();
    }

    private GoPluginApiResponse handleGetPluginSettingsView() throws IOException {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("template", IOUtils.toString(getClass().getResourceAsStream("/plugin-settings.template.html"), "UTF-8"));
        return renderJSON(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, response);
    }

    private GoPluginApiResponse handleValidatePluginSettingsConfiguration(GoPluginApiRequest goPluginApiRequest) {
        List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
        return renderJSON(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, response);
    }

    private GoPluginApiResponse handleGetPluginSettingsConfiguration() {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put(PLUGIN_SETTINGS_FILE_PATTERN, createField(DISPLAY_NAME_FILE_PATTERN, DEFAULT_FILE_PATTERN, false, false, "0"));
        response.put(PLUGIN_SETTINGS_GENERATOR_CONFIG_PATTERN, createField(DISPLAY_NAME_GENERATOR_CONFIG_PATTERN, DEFAULT_GENERATOR_CONFIG_PATTERN, false, false, "0"));
        return renderJSON(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, response);
    }

    private Map<String, Object> createField(String displayName, String defaultValue, boolean isRequired, boolean isSecure, String displayOrder) {
        Map<String, Object> fieldProperties = new HashMap<String, Object>();
        fieldProperties.put("display-name", displayName);
        fieldProperties.put("default-value", defaultValue);
        fieldProperties.put("required", isRequired);
        fieldProperties.put("secure", isSecure);
        fieldProperties.put("display-order", displayOrder);
        return fieldProperties;
    }

    public PluginSettings getPluginSettings() {
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("plugin-id", PLUGIN_ID);
        GoApiResponse response = goApplicationAccessor.submit(createGoApiRequest(GET_PLUGIN_SETTINGS, JSONUtils.toJSON(requestMap)));
        if (response.responseBody() == null || response.responseBody().trim().isEmpty()) {
            return new PluginSettings();
        }
        Map<String, String> responseBodyMap = (Map<String, String>) JSONUtils.fromJSON(response.responseBody());
        return new PluginSettings(
                responseBodyMap.get(PLUGIN_SETTINGS_FILE_PATTERN),
                responseBodyMap.get(PLUGIN_SETTINGS_GENERATOR_CONFIG_PATTERN));
    }

    private GoApiRequest createGoApiRequest(final String api, final String responseBody) {
        return new GoApiRequest() {
            @Override
            public String api() {
                return api;
            }

            @Override
            public String apiVersion() {
                return "2.0";
            }

            @Override
            public GoPluginIdentifier pluginIdentifier() {
                return getGoPluginIdentifier();
            }

            @Override
            public Map<String, String> requestParameters() {
                return null;
            }

            @Override
            public Map<String, String> requestHeaders() {
                return null;
            }

            @Override
            public String requestBody() {
                return responseBody;
            }
        };
    }

    private GoPluginApiResponse renderJSON(final int responseCode, Object response) {
        final String json = response == null ? null : new GsonBuilder().create().toJson(response);
        return new GoPluginApiResponse() {
            @Override
            public int responseCode() {
                return responseCode;
            }

            @Override
            public Map<String, String> responseHeaders() {
                return null;
            }

            @Override
            public String responseBody() {
                return json;
            }
        };
    }
}
