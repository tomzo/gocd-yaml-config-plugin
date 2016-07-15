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
import org.junit.Test;

import java.io.*;

import static cd.go.plugin.config.yaml.TestUtils.getResourceAsStream;
import static cd.go.plugin.config.yaml.TestUtils.readJsonObject;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class YamlConfigPluginIntegrationTest {
    private YamlConfigPlugin plugin;
    private GoApplicationAccessor goAccessor;
    private Gson gson;
    private JsonParser parser;

    @Before
    public void SetUp() throws IOException {
        plugin = new YamlConfigPlugin();
        goAccessor = mock(GoApplicationAccessor.class);
        plugin.initializeGoApplicationAccessor(goAccessor);
        GoApiResponse settingsResponse = DefaultGoApiResponse.success("{}");
        when(goAccessor.submit(any(GoApiRequest.class))).thenReturn(settingsResponse);
        gson = new Gson();
        parser = new JsonParser();

        File emptyDir = new File("emptyDir");
        FileUtils.deleteDirectory(emptyDir);
        FileUtils.forceMkdir(emptyDir);
    }

    @Test
    public void shouldRespondSuccessToGetConfigurationRequest()  throws UnhandledRequestTypeException
    {
        DefaultGoPluginApiRequest getConfigRequest = new DefaultGoPluginApiRequest("configrepo","1.0","go.plugin-settings.get-configuration");

        GoPluginApiResponse response = plugin.handle(getConfigRequest);
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE));
    }

    @Test
    public void shouldContainFilePatternInResponseToGetConfigurationRequest()  throws UnhandledRequestTypeException
    {
        DefaultGoPluginApiRequest getConfigRequest = new DefaultGoPluginApiRequest("configrepo","1.0","go.plugin-settings.get-configuration");

        GoPluginApiResponse response = plugin.handle(getConfigRequest);
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE));
        JsonObject responseJsonObject = getJsonObjectFromResponse(response);
        JsonElement pattern = responseJsonObject.get("file_pattern");
        assertNotNull(pattern);
        JsonObject patternAsJsonObject = pattern.getAsJsonObject();
        assertThat(patternAsJsonObject.get("display-name").getAsString(), is("Go YAML files pattern"));
        assertThat(patternAsJsonObject.get("default-value").getAsString(),is("**/*.gocd.yaml"));
        assertThat(patternAsJsonObject.get("required").getAsBoolean(),is(false));
        assertThat(patternAsJsonObject.get("secure").getAsBoolean(),is(false));
        assertThat(patternAsJsonObject.get("display-order").getAsInt(),is(0));
    }

    private JsonObject getJsonObjectFromResponse(GoPluginApiResponse response) {
        String responseBody = response.responseBody();
        return parser.parse(responseBody).getAsJsonObject();
    }

    @Test
    public void shouldRespondSuccessToGetViewRequest() throws UnhandledRequestTypeException
    {
        DefaultGoPluginApiRequest getConfigRequest = new DefaultGoPluginApiRequest("configrepo","1.0","go.plugin-settings.get-view");

        GoPluginApiResponse response = plugin.handle(getConfigRequest);
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE));
    }

    @Test
    public void shouldRespondSuccessToValidateConfigRequest() throws UnhandledRequestTypeException
    {
        DefaultGoPluginApiRequest validateRequest = new DefaultGoPluginApiRequest("configrepo","1.0","go.plugin-settings.validate-configuration");

        GoPluginApiResponse response = plugin.handle(validateRequest);
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE));
    }

    @Test
    public void shouldRespondSuccessToParseDirectoryRequestWhenEmpty() throws UnhandledRequestTypeException
    {
        DefaultGoPluginApiRequest parseDirectoryRequest = new DefaultGoPluginApiRequest("configrepo","1.0","parse-directory");
        String requestBody = "{\n" +
                "    \"directory\":\"emptyDir\",\n" +
                "    \"configurations\":[]\n" +
                "}";
        parseDirectoryRequest.setRequestBody(requestBody);

        GoPluginApiResponse response = plugin.handle(parseDirectoryRequest);
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE));
        JsonObject responseJsonObject = getJsonObjectFromResponse(response);
        assertThat(responseJsonObject.get("errors"), Is.<JsonElement>is(new JsonArray()));
    }

    @Test
    public void shouldRespondSuccessToParseDirectoryRequestWhenSimpleCaseFile() throws UnhandledRequestTypeException, IOException {
        setupSimpleCase();

        DefaultGoPluginApiRequest parseDirectoryRequest = new DefaultGoPluginApiRequest("configrepo","1.0","parse-directory");
        String requestBody = "{\n" +
                "    \"directory\":\"simpleCase\",\n" +
                "    \"configurations\":[]\n" +
                "}";
        parseDirectoryRequest.setRequestBody(requestBody);

        GoPluginApiResponse response = plugin.handle(parseDirectoryRequest);
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE));
        JsonObject responseJsonObject = getJsonObjectFromResponse(response);
        assertThat(responseJsonObject.get("errors"), Is.<JsonElement>is(new JsonArray()));
        JsonArray pipelines = responseJsonObject.get("pipelines").getAsJsonArray();
        assertThat(pipelines.size(),is(1));
        JsonObject expected = (JsonObject)readJsonObject("examples.out/simple.gocd.json");
        assertThat(responseJsonObject,is(new JsonObjectMatcher(expected)));
    }

    private void setupSimpleCase() throws IOException {
        File simpleCase = new File("simpleCase");
        FileUtils.deleteDirectory(simpleCase);
        FileUtils.forceMkdir(simpleCase);
        File simpleFile = new File("simpleCase/simple.gocd.yaml");
        InputStream in = getResourceAsStream("examples/simple.gocd.yaml");
        OutputStream out = new FileOutputStream(simpleFile);
        IOUtils.copy(in,out);
        in.close();
        out.close();
    }

    @Test
    public void shouldRespondBadRequestToParseDirectoryRequestWhenDirectoryIsNotSpecified() throws UnhandledRequestTypeException
    {
        DefaultGoPluginApiRequest parseDirectoryRequest = new DefaultGoPluginApiRequest("configrepo","1.0","parse-directory");
        String requestBody = "{\n" +
                "    \"configurations\":[]\n" +
                "}";
        parseDirectoryRequest.setRequestBody(requestBody);

        GoPluginApiResponse response = plugin.handle(parseDirectoryRequest);
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.BAD_REQUEST));
    }
    @Test
    public void shouldRespondBadRequestToParseDirectoryRequestWhenRequestBodyIsNull() throws UnhandledRequestTypeException
    {
        DefaultGoPluginApiRequest parseDirectoryRequest = new DefaultGoPluginApiRequest("configrepo","1.0","parse-directory");
        String requestBody = null;
        parseDirectoryRequest.setRequestBody(requestBody);

        GoPluginApiResponse response = plugin.handle(parseDirectoryRequest);
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.BAD_REQUEST));
    }
    @Test
    public void shouldRespondBadRequestToParseDirectoryRequestWhenRequestBodyIsEmpty() throws UnhandledRequestTypeException
    {
        DefaultGoPluginApiRequest parseDirectoryRequest = new DefaultGoPluginApiRequest("configrepo","1.0","parse-directory");
        parseDirectoryRequest.setRequestBody("{}");

        GoPluginApiResponse response = plugin.handle(parseDirectoryRequest);
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.BAD_REQUEST));
    }
    @Test
    public void shouldRespondBadRequestToParseDirectoryRequestWhenRequestBodyIsNotJson() throws UnhandledRequestTypeException
    {
        DefaultGoPluginApiRequest parseDirectoryRequest = new DefaultGoPluginApiRequest("configrepo","1.0","parse-directory");
        parseDirectoryRequest.setRequestBody("{bla");

        GoPluginApiResponse response = plugin.handle(parseDirectoryRequest);
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.BAD_REQUEST));
    }

    @Test
    public void shouldTalkToGoApplicationAccessorToGetPluginSettings() throws UnhandledRequestTypeException
    {
        DefaultGoPluginApiRequest parseDirectoryRequest = new DefaultGoPluginApiRequest("configrepo","1.0","parse-directory");
        String requestBody = "{\n" +
                "    \"directory\":\"emptyDir\",\n" +
                "    \"configurations\":[]\n" +
                "}";
        parseDirectoryRequest.setRequestBody(requestBody);

        GoPluginApiResponse response = plugin.handle(parseDirectoryRequest);

        verify(goAccessor,times(1)).submit(any(GoApiRequest.class));
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE));
    }

    @Test
    public void shouldRespondSuccessToParseDirectoryRequestWhenPluginHasConfiguration() throws UnhandledRequestTypeException
    {
        GoApiResponse settingsResponse = DefaultGoApiResponse.success("{}");
        when(goAccessor.submit(any(GoApiRequest.class))).thenReturn(settingsResponse);

        DefaultGoPluginApiRequest parseDirectoryRequest = new DefaultGoPluginApiRequest("configrepo","1.0","parse-directory");
        String requestBody = "{\n" +
                "    \"directory\":\"emptyDir\",\n" +
                "    \"configurations\":[]\n" +
                "}";
        parseDirectoryRequest.setRequestBody(requestBody);

        GoPluginApiResponse response = plugin.handle(parseDirectoryRequest);

        verify(goAccessor,times(1)).submit(any(GoApiRequest.class));
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE));
    }

    @Test
    public void shouldContainValidFieldsInResponseMessage() throws UnhandledRequestTypeException
    {
        GoApiResponse settingsResponse = DefaultGoApiResponse.success("{}");
        when(goAccessor.submit(any(GoApiRequest.class))).thenReturn(settingsResponse);

        DefaultGoPluginApiRequest parseDirectoryRequest = new DefaultGoPluginApiRequest("configrepo","1.0","parse-directory");
        String requestBody = "{\n" +
                "    \"directory\":\"emptyDir\",\n" +
                "    \"configurations\":[]\n" +
                "}";
        parseDirectoryRequest.setRequestBody(requestBody);

        GoPluginApiResponse response = plugin.handle(parseDirectoryRequest);

        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE));
        final JsonParser parser = new JsonParser();
        JsonElement responseObj = parser.parse(response.responseBody());
        assertTrue(responseObj.isJsonObject());
        JsonObject obj = responseObj.getAsJsonObject();
        assertTrue(obj.has("errors"));
        assertTrue(obj.has("pipelines"));
        assertTrue(obj.has("environments"));
        assertTrue(obj.has("target_version"));
    }
}