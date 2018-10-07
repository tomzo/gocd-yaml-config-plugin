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

public class TemplateTransformTest {

    private TemplateTransform parser;
    private StageTransform stageTransform;

    @Before
    public void SetUp() {
        stageTransform = mock(StageTransform.class);
        parser = new TemplateTransform(stageTransform);
    }

    @Test
    public void shouldTransformSimpleTemplate() throws IOException {
        testTransform("simple.pipe");
    }

    private void testTransform(String caseFile) throws IOException {
        testTransform(caseFile, caseFile);
    }

    private void testTransform(String caseFile, String expectedFile) throws IOException {
        JsonObject expectedObject = (JsonObject) readJsonObject("parts/templates/" + expectedFile + ".json");
        JsonObject jsonObject = parser.transform(readYamlObject("parts/templates/" + caseFile + ".yaml"));
        assertThat(jsonObject, is(new JsonObjectMatcher(expectedObject)));
    }
}
