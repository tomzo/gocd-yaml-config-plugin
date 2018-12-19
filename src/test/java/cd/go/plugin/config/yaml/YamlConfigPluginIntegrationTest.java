package cd.go.plugin.config.yaml;

import com.google.gson.*;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;

import static cd.go.plugin.config.yaml.TestUtils.getResourceAsStream;
import static cd.go.plugin.config.yaml.TestUtils.readJsonObject;
import static com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class YamlConfigPluginIntegrationTest {
    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();
    private YamlConfigPlugin plugin;
    private GoApplicationAccessor goAccessor;
    private JsonParser parser;

    @Before
    public void setUp() {
        plugin = new YamlConfigPlugin();
        goAccessor = mock(GoApplicationAccessor.class);
        plugin.initializeGoApplicationAccessor(goAccessor);
        GoApiResponse settingsResponse = DefaultGoApiResponse.success("{}");
        when(goAccessor.submit(any(GoApiRequest.class))).thenReturn(settingsResponse);
        parser = new JsonParser();
    }

    @Test
    public void respondsToParseContentRequest() throws Exception {
        final Gson gson = new Gson();
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest("configrepo", "2.0", ConfigRepoMessages.REQ_PARSE_CONTENT);

        StringWriter w = new StringWriter();
        IOUtils.copy(getResourceAsStream("examples/simple.gocd.yaml"), w);
        request.setRequestBody(gson.toJson(Collections.singletonMap("content", w.toString())));

        GoPluginApiResponse response = plugin.handle(request);
        assertEquals(SUCCESS_RESPONSE_CODE, response.responseCode());
        JsonObject responseJsonObject = getJsonObjectFromResponse(response);
        assertNoError(responseJsonObject);

        JsonArray pipelines = responseJsonObject.get("pipelines").getAsJsonArray();
        assertThat(pipelines.size(), is(1));
        JsonObject expected = (JsonObject) readJsonObject("examples.out/simple.gocd.json");
        expected.getAsJsonArray("pipelines").get(0).getAsJsonObject().addProperty("location", "content");
        assertThat(responseJsonObject, is(new JsonObjectMatcher(expected)));
    }

    @Test
    public void shouldRespondSuccessToGetConfigurationRequest() throws UnhandledRequestTypeException {
        DefaultGoPluginApiRequest getConfigRequest = new DefaultGoPluginApiRequest("configrepo", "1.0", "go.plugin-settings.get-configuration");

        GoPluginApiResponse response = plugin.handle(getConfigRequest);
        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
    }

    @Test
    public void shouldContainFilePatternInResponseToGetConfigurationRequest() throws UnhandledRequestTypeException {
        DefaultGoPluginApiRequest getConfigRequest = new DefaultGoPluginApiRequest("configrepo", "1.0", "go.plugin-settings.get-configuration");

        GoPluginApiResponse response = plugin.handle(getConfigRequest);
        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
        JsonObject responseJsonObject = getJsonObjectFromResponse(response);
        JsonElement pattern = responseJsonObject.get("file_pattern");
        assertNotNull(pattern);
        JsonObject patternAsJsonObject = pattern.getAsJsonObject();
        assertThat(patternAsJsonObject.get("display-name").getAsString(), is("Go YAML files pattern"));
        assertThat(patternAsJsonObject.get("default-value").getAsString(), is("**/*.gocd.yaml,**/*.gocd.yml"));
        assertThat(patternAsJsonObject.get("required").getAsBoolean(), is(false));
        assertThat(patternAsJsonObject.get("secure").getAsBoolean(), is(false));
        assertThat(patternAsJsonObject.get("display-order").getAsInt(), is(0));
    }

    @Test
    public void shouldRespondSuccessToGetViewRequest() throws UnhandledRequestTypeException {
        DefaultGoPluginApiRequest getConfigRequest = new DefaultGoPluginApiRequest("configrepo", "1.0", "go.plugin-settings.get-view");

        GoPluginApiResponse response = plugin.handle(getConfigRequest);
        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
    }

    @Test
    public void shouldRespondSuccessToValidateConfigRequest() throws UnhandledRequestTypeException {
        DefaultGoPluginApiRequest validateRequest = new DefaultGoPluginApiRequest("configrepo", "1.0", "go.plugin-settings.validate-configuration");

        GoPluginApiResponse response = plugin.handle(validateRequest);
        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
    }

    @Test
    public void shouldRespondSuccessToParseDirectoryRequestWhenEmpty() throws UnhandledRequestTypeException {
        GoPluginApiResponse response = parseAndGetResponseForDir(tempDir.getRoot());

        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
        JsonObject responseJsonObject = getJsonObjectFromResponse(response);
        assertNoError(responseJsonObject);
    }

    @Test
    public void shouldRespondSuccessToParseDirectoryRequestWhenSimpleCaseFile() throws UnhandledRequestTypeException, IOException {
        GoPluginApiResponse response = parseAndGetResponseForDir(setupCase("simple"));

        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
        JsonObject responseJsonObject = getJsonObjectFromResponse(response);
        assertNoError(responseJsonObject);
        JsonArray pipelines = responseJsonObject.get("pipelines").getAsJsonArray();
        assertThat(pipelines.size(), is(1));
        JsonObject expected = (JsonObject) readJsonObject("examples.out/simple.gocd.json");
        assertThat(responseJsonObject, is(new JsonObjectMatcher(expected)));
    }

    @Test
    public void shouldRespondSuccessToParseDirectoryRequestWhenRichCaseFile() throws UnhandledRequestTypeException, IOException {
        GoPluginApiResponse response = parseAndGetResponseForDir(setupCase("rich"));

        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
        JsonObject responseJsonObject = getJsonObjectFromResponse(response);
        assertNoError(responseJsonObject);
        JsonArray pipelines = responseJsonObject.get("pipelines").getAsJsonArray();
        assertThat(pipelines.size(), is(1));
        JsonObject expected = (JsonObject) readJsonObject("examples.out/rich.gocd.json");
        assertThat(responseJsonObject, is(new JsonObjectMatcher(expected)));
    }

    @Test
    public void shouldRespondSuccessWithErrorMessagesToParseDirectoryRequestWhenSimpleInvalidCaseFile() throws UnhandledRequestTypeException, IOException {
        GoPluginApiResponse response = parseAndGetResponseForDir(setupCase("simple-invalid"));

        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
        JsonObject responseJsonObject = getJsonObjectFromResponse(response);
        JsonArray pipelines = responseJsonObject.get("pipelines").getAsJsonArray();
        assertThat(pipelines.size(), is(0));
        assertFirstError(responseJsonObject, "Failed to parse pipeline pipe1; expected a hash of pipeline materials", "simple-invalid.gocd.yaml");
    }

    @Test
    public void shouldRespondSuccessWithErrorMessagesToParseDirectoryRequestWhenDuplicateKeysCaseFile() throws UnhandledRequestTypeException, IOException {
        GoPluginApiResponse response = parseAndGetResponseForDir(setupCase("duplicate-materials"));

        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
        JsonObject responseJsonObject = getJsonObjectFromResponse(response);
        JsonArray pipelines = responseJsonObject.get("pipelines").getAsJsonArray();
        assertThat(pipelines.size(), is(0));
        assertFirstError(responseJsonObject, "Line 9, column 20: Duplicate key found 'upstream'", "duplicate-materials.gocd.yaml");
    }

    @Test
    public void shouldRespondSuccessWithErrorMessagesToParseDirectoryRequestWhenParsingErrorCaseFile() throws UnhandledRequestTypeException, IOException {
        GoPluginApiResponse response = parseAndGetResponseForDir(setupCase("invalid-materials"));

        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
        JsonObject responseJsonObject = getJsonObjectFromResponse(response);
        JsonArray pipelines = responseJsonObject.get("pipelines").getAsJsonArray();
        assertThat(pipelines.size(), is(0));
        assertFirstError(responseJsonObject, "Error parsing YAML. : Line 21, column 0: Expected a 'block end' but found: scalar : ", "invalid-materials.gocd.yaml");
    }

    @Test
    public void shouldRespondBadRequestToParseDirectoryRequestWhenDirectoryIsNotSpecified() throws UnhandledRequestTypeException {
        DefaultGoPluginApiRequest parseDirectoryRequest = new DefaultGoPluginApiRequest("configrepo", "1.0", "parse-directory");
        String requestBody = "{\n" +
                "    \"configurations\":[]\n" +
                "}";
        parseDirectoryRequest.setRequestBody(requestBody);

        GoPluginApiResponse response = plugin.handle(parseDirectoryRequest);
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.BAD_REQUEST));
    }

    @Test
    public void shouldParseDirectoryWithCustomPatternWhenInConfigurations() throws UnhandledRequestTypeException, IOException {
        File simpleCaseDir = setupCase("simple", "go.yml");
        DefaultGoPluginApiRequest parseDirectoryRequest = new DefaultGoPluginApiRequest("configrepo", "1.0", "parse-directory");
        String requestBody = "{\n" +
                "    \"directory\":\"" + simpleCaseDir + "\",\n" +
                "    \"configurations\":[" +
                "{" +
                "\"key\" : \"file_pattern\"," +
                "\"value\" : \"simple.go.yml\" " +
                "}" +
                "]\n" +
                "}";
        parseDirectoryRequest.setRequestBody(requestBody);

        GoPluginApiResponse response = plugin.handle(parseDirectoryRequest);
        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
        JsonObject responseJsonObject = getJsonObjectFromResponse(response);
        assertNoError(responseJsonObject);
        JsonArray pipelines = responseJsonObject.get("pipelines").getAsJsonArray();
        assertThat(pipelines.size(), is(1));
    }

    @Test
    public void shouldRespondBadRequestToParseDirectoryRequestWhenRequestBodyIsNull() throws UnhandledRequestTypeException {
        DefaultGoPluginApiRequest parseDirectoryRequest = new DefaultGoPluginApiRequest("configrepo", "1.0", "parse-directory");
        String requestBody = null;
        parseDirectoryRequest.setRequestBody(requestBody);

        GoPluginApiResponse response = plugin.handle(parseDirectoryRequest);
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.BAD_REQUEST));
    }

    @Test
    public void shouldRespondBadRequestToParseDirectoryRequestWhenRequestBodyIsEmpty() throws UnhandledRequestTypeException {
        DefaultGoPluginApiRequest parseDirectoryRequest = new DefaultGoPluginApiRequest("configrepo", "1.0", "parse-directory");
        parseDirectoryRequest.setRequestBody("{}");

        GoPluginApiResponse response = plugin.handle(parseDirectoryRequest);
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.BAD_REQUEST));
    }

    @Test
    public void shouldRespondBadRequestToParseDirectoryRequestWhenRequestBodyIsNotJson() throws UnhandledRequestTypeException {
        DefaultGoPluginApiRequest parseDirectoryRequest = new DefaultGoPluginApiRequest("configrepo", "1.0", "parse-directory");
        parseDirectoryRequest.setRequestBody("{bla");

        GoPluginApiResponse response = plugin.handle(parseDirectoryRequest);
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.BAD_REQUEST));
    }

    @Test
    public void shouldTalkToGoApplicationAccessorToGetPluginSettings() throws UnhandledRequestTypeException {
        GoPluginApiResponse response = parseAndGetResponseForDir(tempDir.getRoot());

        verify(goAccessor, times(1)).submit(any(GoApiRequest.class));
        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
    }

    @Test
    public void shouldRespondSuccessToParseDirectoryRequestWhenPluginHasConfiguration() throws UnhandledRequestTypeException {
        GoApiResponse settingsResponse = DefaultGoApiResponse.success("{}");
        when(goAccessor.submit(any(GoApiRequest.class))).thenReturn(settingsResponse);

        GoPluginApiResponse response = parseAndGetResponseForDir(tempDir.getRoot());

        verify(goAccessor, times(1)).submit(any(GoApiRequest.class));
        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
    }

    @Test
    public void shouldContainValidFieldsInResponseMessage() throws UnhandledRequestTypeException {
        GoApiResponse settingsResponse = DefaultGoApiResponse.success("{}");
        when(goAccessor.submit(any(GoApiRequest.class))).thenReturn(settingsResponse);

        GoPluginApiResponse response = parseAndGetResponseForDir(tempDir.getRoot());

        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
        final JsonParser parser = new JsonParser();
        JsonElement responseObj = parser.parse(response.responseBody());
        assertTrue(responseObj.isJsonObject());
        JsonObject obj = responseObj.getAsJsonObject();
        assertTrue(obj.has("errors"));
        assertTrue(obj.has("pipelines"));
        assertTrue(obj.has("environments"));
        assertTrue(obj.has("target_version"));
    }

    @Test
    public void shouldRespondSuccessToParseDirectoryRequestWhenAliasesCaseFile() throws UnhandledRequestTypeException,
            IOException {
        GoPluginApiResponse response = parseAndGetResponseForDir(setupCase("aliases"));

        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
        JsonObject responseJsonObject = getJsonObjectFromResponse(response);
        assertNoError(responseJsonObject);
        JsonArray pipelines = responseJsonObject.get("pipelines").getAsJsonArray();
        assertThat(pipelines.size(), is(1));
        JsonObject expected = (JsonObject) readJsonObject("examples.out/aliases.gocd.json");
        assertThat(responseJsonObject, is(new JsonObjectMatcher(expected)));
    }

    @Test
    public void shouldUpdateTargetVersionWhenItIsTheSameAcrossAllFiles() throws Exception {
        FileUtils.copyInputStreamToFile(getResourceAsStream("/parts/roots/version_2.yaml"), tempDir.newFile("v2_1.gocd.yaml"));
        FileUtils.copyInputStreamToFile(getResourceAsStream("/parts/roots/version_2.yaml"), tempDir.newFile("v2_2.gocd.yaml"));

        GoPluginApiResponse response = parseAndGetResponseForDir(tempDir.getRoot());
        assertNoError(getJsonObjectFromResponse(response));
    }

    @Test
    public void shouldUpdateTargetVersionWhenItIsTheDefaultOrMissingAcrossAllPipelinesAndEnvironments() throws Exception {
        FileUtils.copyInputStreamToFile(getResourceAsStream("/parts/roots/version_1.yaml"), tempDir.newFile("v1_1.gocd.yaml"));
        FileUtils.copyInputStreamToFile(getResourceAsStream("/parts/roots/version_not_present.yaml"), tempDir.newFile("v1_not_present.gocd.yaml"));
        FileUtils.copyInputStreamToFile(getResourceAsStream("/parts/roots/version_1.yaml"), tempDir.newFile("v1_2.gocd.yaml"));

        GoPluginApiResponse response = parseAndGetResponseForDir(tempDir.getRoot());
        assertNoError(getJsonObjectFromResponse(response));
    }

    @Test
    public void shouldFailToUpdateTargetVersionWhenItIs_NOT_TheSameAcrossAllFiles() throws Exception {
        FileUtils.copyInputStreamToFile(getResourceAsStream("/parts/roots/version_1.yaml"), tempDir.newFile("v1_1.gocd.yaml"));
        FileUtils.copyInputStreamToFile(getResourceAsStream("/parts/roots/version_1.yaml"), tempDir.newFile("v1_2.gocd.yaml"));
        FileUtils.copyInputStreamToFile(getResourceAsStream("/parts/roots/version_2.yaml"), tempDir.newFile("v2_1.gocd.yaml"));

        GoPluginApiResponse response = parseAndGetResponseForDir(tempDir.getRoot());
        String expectedFailureMessage = "java.lang.RuntimeException: Versions across files are not unique. Found" +
                " versions: [1, 2]. There can only be one version across the whole repository.";
        assertFirstError(getJsonObjectFromResponse(response), expectedFailureMessage, "YAML config plugin");
    }

    @Test
    public void shouldRespondWithCapabilities() throws UnhandledRequestTypeException {
        String expected = new Gson().toJson(new Capabilities());
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest("configrepo", "2.0", "get-capabilities");

        GoPluginApiResponse response = plugin.handle(request);

        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
        assertThat(response.responseBody(), is(expected));
    }

    private File setupCase(String caseName) throws IOException {
        return setupCase(caseName, "gocd.yaml");
    }

    private File setupCase(String caseName, String extension) throws IOException {
        File simpleFile = tempDir.newFile(caseName + "." + extension);
        FileUtils.copyInputStreamToFile(getResourceAsStream("examples/" + caseName + ".gocd.yaml"), simpleFile);
        return tempDir.getRoot();
    }

    private GoPluginApiResponse parseAndGetResponseForDir(File directory) throws UnhandledRequestTypeException {
        DefaultGoPluginApiRequest parseDirectoryRequest = new DefaultGoPluginApiRequest("configrepo", "1.0", "parse-directory");
        String requestBody = "{\n" +
                "    \"directory\":\"" + directory + "\",\n" +
                "    \"configurations\":[]\n" +
                "}";
        parseDirectoryRequest.setRequestBody(requestBody);

        return plugin.handle(parseDirectoryRequest);
    }

    private void assertNoError(JsonObject responseJsonObject) {
        assertThat(responseJsonObject.get("errors"), Is.<JsonElement>is(new JsonArray()));
    }

    private void assertFirstError(JsonObject responseJsonObject, String expectedMessage, String expectedLocation) {
        JsonArray errors = (JsonArray) responseJsonObject.get("errors");
        assertThat(errors.get(0).getAsJsonObject().getAsJsonPrimitive("message").getAsString(), is(expectedMessage));
        assertThat(errors.get(0).getAsJsonObject().getAsJsonPrimitive("location").getAsString(), is(expectedLocation));
    }

    private JsonObject getJsonObjectFromResponse(GoPluginApiResponse response) {
        String responseBody = response.responseBody();
        return parser.parse(responseBody).getAsJsonObject();
    }
}
