package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.JsonObjectMatcher;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static cd.go.plugin.config.yaml.TestUtils.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StageTransformTest {
    private StageTransform parser;
    private EnvironmentVariablesTransform environmentTransform;
    private JobTransform jobTransform;

    @Before
    public void SetUp() {
        environmentTransform = new EnvironmentVariablesTransform();
        jobTransform = mock(JobTransform.class);
        parser = new StageTransform(environmentTransform, jobTransform);
    }

    @Test
    public void shouldTransformCompleteStage() throws IOException {
        testTransform("complete");
    }

    @Test
    public void shouldTransformShortApprovalStage() throws IOException {
        testTransform("short_approval");
    }

    @Test
    public void shouldTransformSingleJobStage() throws IOException {
        parser = new StageTransform(environmentTransform, new JobTransform(environmentTransform, mock(TaskTransform.class)));
        testTransform("stage-job");
    }

    @Test
    public void shouldInverseTransformCompleteStage() throws IOException {
        Map<String, Object> jobs = new LinkedTreeMap<>();
        jobs.put("one", null);
        jobs.put("two", null);
        when(jobTransform.inverseTransform(any(LinkedTreeMap.class))).thenReturn(jobs);
        testInverseTransform("complete");
    }

    @Test
    public void shouldInverseTransformSingleJobStage() throws IOException {
        parser = new StageTransform(environmentTransform, new JobTransform(environmentTransform, mock(TaskTransform.class)));
        testTransform("stage-job");
    }

    private void testTransform(String caseFile) throws IOException {
        testTransform(caseFile, caseFile);
    }

    private void testTransform(String caseFile, String expectedFile) throws IOException {
        JsonObject expectedObject = (JsonObject) readJsonObject("parts/stages/" + expectedFile + ".json");
        JsonObject jsonObject = parser.transform(readYamlObject("parts/stages/" + caseFile + ".yaml"));
        assertThat(jsonObject, is(new JsonObjectMatcher(expectedObject)));
    }

    private void testInverseTransform(String caseFile) throws IOException {
        testInverseTransform(caseFile, caseFile);
    }

    private void testInverseTransform(String caseFile, String expectedFile) throws IOException {
        Map<String, Object> inverse = parser.inverseTransform(readJsonGson("parts/stages/" + caseFile + ".json"));
        JsonObject actual = parser.transform(inverse);
        assertEquals((readJsonObject("parts/stages/" + expectedFile + ".json")), actual);
    }
}