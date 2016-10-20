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

public class PipelineTransformTest {

    private PipelineTransform parser;
    private MaterialTransform materialTransform;
    private StageTransform stageTransform;
    private EnvironmentVariablesTransform environmentTransform;

    @Before
    public void SetUp() {
        materialTransform = mock(MaterialTransform.class);
        stageTransform = mock(StageTransform.class);
        environmentTransform = mock(EnvironmentVariablesTransform.class);
        parser = new PipelineTransform(materialTransform, stageTransform, environmentTransform);
    }

    @Test
    public void shouldTransformSimplePipeline() throws IOException {
        testTransform("simple.pipe");
    }

    @Test
    public void shouldTransformRichPipeline() throws IOException {
        testTransform("rich.pipe");
    }

    private void testTransform(String caseFile) throws IOException {
        testTransform(caseFile, caseFile);
    }

    private void testTransform(String caseFile, String expectedFile) throws IOException {
        JsonObject expectedObject = (JsonObject) readJsonObject("parts/" + expectedFile + ".json");
        JsonObject jsonObject = parser.transform(readYamlObject("parts/" + caseFile + ".yaml"));
        assertThat(jsonObject, is(new JsonObjectMatcher(expectedObject)));
    }
}