package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.JsonObjectMatcher;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static cd.go.plugin.config.yaml.TestUtils.readJsonObject;
import static cd.go.plugin.config.yaml.TestUtils.readYamlObject;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class JobTransformTest {
    private EnvironmentVariablesTransform environmentTransform;
    private TaskTransform taskTransform;
    private JobTransform parser;

    @Before
    public void SetUp() {
        environmentTransform = new EnvironmentVariablesTransform();
        taskTransform = mock(TaskTransform.class);
        parser = new JobTransform(environmentTransform, taskTransform);
    }

    @Test
    public void shouldTransformMinimalJob() throws IOException {
        testTransform("minimal");
    }

    @Test
    public void shouldTransformRunAllJob() throws IOException {
        JsonObject job = testTransform("runall");
        assertThat(job.get("run_instance_count").getAsJsonPrimitive().isString(), is(true));
        assertThat(job.get("run_instance_count").getAsString(), is("all"));
    }

    @Test
    public void shouldTransformCompleteJob() throws IOException {
        testTransform("complete");
    }

    @Test
    public void shouldTransformElasticProfileJob() throws IOException {
        testTransform("elastic_profile");
    }

    @Test
    public void shouldTransformJobWithListOfListsTasks() throws IOException {
        environmentTransform = new EnvironmentVariablesTransform();
        taskTransform = new TaskTransform();
        parser = new JobTransform(environmentTransform, taskTransform);

        JsonObject job = testTransform("list_of_lists_tasks");
    }

    private JsonObject testTransform(String caseFile) throws IOException {
        return testTransform(caseFile, caseFile);
    }

    private JsonObject testTransform(String caseFile, String expectedFile) throws IOException {
        JsonObject expectedObject = (JsonObject) readJsonObject("parts/jobs/" + expectedFile + ".json");
        JsonObject jsonObject = parser.transform(readYamlObject("parts/jobs/" + caseFile + ".yaml"));
        assertThat(jsonObject, is(new JsonObjectMatcher(expectedObject)));
        return jsonObject;
    }
}