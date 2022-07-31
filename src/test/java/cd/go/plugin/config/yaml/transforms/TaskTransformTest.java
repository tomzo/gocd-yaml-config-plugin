package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.JsonObjectMatcher;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static cd.go.plugin.config.yaml.TestUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTransformTest {
    private final TaskTransform parser;

    public TaskTransformTest() {
        parser = new TaskTransform();
    }

    @Test
    public void shouldTransformMinimalFetch() throws IOException {
        testTransform("minimal.fetch");
    }

    @Test
    public void shouldTransformCompleteFetch() throws IOException {
        testTransform("complete.fetch");
    }

    @Test
    public void shouldTransformFetchExternalArtifactTask() throws IOException {
        testTransform("fetch_external_artifact");
    }

    @Test
    public void shouldTransformCompleteRake() throws IOException {
        testTransform("complete.rake");
    }

    @Test
    public void shouldTransformCompletePlugin() throws IOException {
        testTransform("complete.plugin");
    }

    @Test
    public void shouldTransformMinimalRake() throws IOException {
        testTransform("minimal.rake");
    }

    @Test
    public void shouldTransformFullExec() throws IOException {
        testTransform("full.exec");
    }

    @Test
    public void shouldTransformScript() throws IOException {
        testTransform("script");
    }

    @Test
    public void shouldTransformMultilineScript() throws IOException {
        JsonObject plugin = testTransform("multiline.script");
        assertThat(plugin.getAsJsonArray("configuration").get(0).getAsJsonObject().get("value").getAsString(), is("./build.sh compile\nmake test"));
    }

    @Test
    public void shouldTransformBreaklineScript() throws IOException {
        JsonObject plugin = testTransform("breakline.script");
        assertThat(plugin.getAsJsonArray("configuration").get(0).getAsJsonObject().get("value").getAsString(), is("./build.sh compile && make test"));
    }

    @Test
    public void shouldInverseTransformFullExec() throws IOException {
        testInverseTransform("full.exec");
    }

    @Test
    public void shouldInverseTransformMinimalFetch() throws IOException {
        testInverseTransform("minimal.fetch");
    }

    @Test
    public void shouldInverseTransformCompleteFetch() throws IOException {
        testInverseTransform("complete.fetch");
    }

    @Test
    public void shouldInverseTransformCompleteRake() throws IOException {
        testInverseTransform("complete.rake");
    }

    @Test
    public void shouldInverseTransformCompletePlugin() throws IOException {
        testInverseTransform("complete.plugin");
    }

    @Test
    public void shouldInverseTransformMinimalRake() throws IOException {
        testInverseTransform("minimal.rake");
    }

    @Test
    public void shouldInverseTransformScript() throws IOException {
        testInverseTransform("script");
    }

    @Test
    public void shouldInverseTransformMultilineScript() throws IOException {
        testInverseTransform("multiline.script");
    }

    @Test
    public void shouldInverseTransformBreaklineScript() throws IOException {
        testInverseTransform("breakline.script");
    }

    private JsonObject testTransform(String caseFile) throws IOException {
        return testTransform(caseFile, caseFile);
    }

    private JsonObject testTransform(String caseFile, String expectedFile) throws IOException {
        JsonObject expectedObject = (JsonObject) readJsonObject("parts/tasks/" + expectedFile + ".json");
        JsonObject jsonObject = parser.transform(readYamlObject("parts/tasks/" + caseFile + ".yaml"));
        assertThat(jsonObject, is(new JsonObjectMatcher(expectedObject)));
        return jsonObject;
    }

    private void testInverseTransform(String caseFile) throws IOException {
        testInverseTransform(caseFile, caseFile);
    }

    private void testInverseTransform(String caseFile, String expectedFile) throws IOException {
        Map<String, Object> inverse = parser.inverseTransform(readJsonGson("parts/tasks/" + caseFile + ".json"));
        JsonObject actual = parser.transform(inverse);
        assertEquals((readJsonObject("parts/tasks/" + expectedFile + ".json")), actual);
    }
}