package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.JsonObjectMatcher;
import cd.go.plugin.config.yaml.YamlUtils;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static cd.go.plugin.config.yaml.TestUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class JobTransformTest {
    private EnvironmentVariablesTransform environmentTransform;
    private TaskTransform taskTransform;
    private JobTransform parser;

    @BeforeEach
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
    public void shouldTransformExternalArtifactConfig() throws IOException {
        useParserWithoutMocks();

        testTransform("external_artifacts");
    }

    @Test
    public void shouldTransformJobWithListOfListsTasks() throws IOException {
        useParserWithoutMocks();

        testTransform("list_of_lists_tasks");
    }

    @Test
    public void shouldInverseTransformMinimalJob() throws IOException {
        testInverseTransform("minimal");
    }

    @Test
    public void shouldInverseTransformMinimalWithDefaultsJob() throws IOException {
        testInverseTransform("minimal_defaults");
    }

    @Test
    public void shouldInverseTransformRunAllJob() throws IOException {
        testInverseTransform("runall");
    }

    @Test
    public void shouldInverseTransformCompleteJob() throws IOException {
        testInverseTransform("complete");
    }

    @Test
    public void shouldInverseTransformElasticProfileJob() throws IOException {
        testInverseTransform("elastic_profile");
    }

    @Test
    public void shouldInverseTransformExternalArtifactConfig() throws IOException {
        useParserWithoutMocks();

        testInverseTransform("external_artifacts");
    }

    private void useParserWithoutMocks() {
        environmentTransform = new EnvironmentVariablesTransform();
        taskTransform = new TaskTransform();
        parser = new JobTransform(environmentTransform, taskTransform);
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

    private void testInverseTransform(String caseFile) throws IOException {
        testInverseTransform(caseFile, caseFile);
    }

    private void testInverseTransform(String caseFile, String expectedFile) throws IOException {
        Map<String, Object> inverse = parser.inverseTransform(readJsonGson("parts/jobs/" + caseFile + ".json"));
        assertYamlEquivalent(loadString("parts/jobs/" + expectedFile + ".yaml"), YamlUtils.dump(inverse));
    }
}