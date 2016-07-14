package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.JsonObjectMatcher;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static cd.go.plugin.config.yaml.TestUtils.readJsonObject;
import static cd.go.plugin.config.yaml.TestUtils.readYamlObject;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class StageTransformTest {
    private StageTransform parser;
    private EnvironmentVariablesTransform environmentTransform;
    private JobTransform jobTransform;

    @Before
    public void SetUp() {
        environmentTransform = new EnvironmentVariablesTransform();
        jobTransform = mock(JobTransform.class);
        parser = new StageTransform(environmentTransform,jobTransform);
    }

    @Test
    public void shouldTransformCompleteStage() throws IOException {
        testTransform("complete");
    }

    @Test
    public void shouldTransformShortApprovalStage() throws IOException {
        testTransform("short_approval");
    }

    private void testTransform(String caseFile) throws IOException {
        testTransform(caseFile,caseFile);
    }

    private void testTransform(String caseFile,String expectedFile) throws IOException {
        JsonObject expectedObject = (JsonObject)readJsonObject("parts/stages/" + expectedFile + ".json");
        JsonObject jsonObject = parser.transform(readYamlObject("parts/stages/" + caseFile + ".yaml"));
        assertThat(jsonObject,is(new JsonObjectMatcher(expectedObject)));
    }
}