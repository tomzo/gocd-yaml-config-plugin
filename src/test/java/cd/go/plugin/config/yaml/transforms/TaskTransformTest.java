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

    private void testTransform(String caseFile) throws IOException {
        testTransform(caseFile,caseFile);
    }

    private void testTransform(String caseFile,String expectedFile) throws IOException {
        JsonObject expectedObject = (JsonObject)readJsonObject("parts/tasks/" + expectedFile + ".json");
        JsonObject jsonObject = parser.transform(readYamlObject("parts/tasks/" + caseFile + ".yaml"));
        assertThat(jsonObject,is(new JsonObjectMatcher(expectedObject)));
    }
}