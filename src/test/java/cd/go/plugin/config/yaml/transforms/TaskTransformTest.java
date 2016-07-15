package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.JsonObjectMatcher;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.io.IOException;

import static cd.go.plugin.config.yaml.TestUtils.readJsonObject;
import static cd.go.plugin.config.yaml.TestUtils.readYamlObject;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

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
        JsonObject plugin = testTransform("script_multiline");
        assertThat(plugin.getAsJsonArray("configuration").get(0).getAsJsonObject().get("value").getAsString(),is("./build.sh compile\nmake test"));
    }

    private JsonObject testTransform(String caseFile) throws IOException {
        return testTransform(caseFile,caseFile);
    }

    private JsonObject testTransform(String caseFile,String expectedFile) throws IOException {
        JsonObject expectedObject = (JsonObject)readJsonObject("parts/tasks/" + expectedFile + ".json");
        JsonObject jsonObject = parser.transform(readYamlObject("parts/tasks/" + caseFile + ".yaml"));
        assertThat(jsonObject,is(new JsonObjectMatcher(expectedObject)));
        return jsonObject;
    }
}