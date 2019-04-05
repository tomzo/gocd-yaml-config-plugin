package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.JsonObjectMatcher;
import cd.go.plugin.config.yaml.YamlUtils;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static cd.go.plugin.config.yaml.TestUtils.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PipelineTransformTest {

    private PipelineTransform parser;
    private MaterialTransform materialTransform;

    @Before
    public void SetUp() {
        materialTransform = mock(MaterialTransform.class);
        StageTransform stageTransform = mock(StageTransform.class);
        EnvironmentVariablesTransform environmentTransform = mock(EnvironmentVariablesTransform.class);
        ParameterTransform parameterTransform = mock(ParameterTransform.class);

        parser = new PipelineTransform(materialTransform, stageTransform, environmentTransform, parameterTransform);
    }

    @Test
    public void shouldTransformSimplePipeline() throws IOException {
        testTransform("simple.pipe");
    }
    @Test
    public void shouldTransformPipelineWithNestedStagesList() throws IOException {
        testTransform("stage_nested_list.pipe");
    }

    @Test
    public void shouldTransformRichPipeline() throws IOException {
        testTransform("rich.pipe");
    }

    @Test
    public void shouldTransformRichPipeline2() throws IOException {
        testTransform("lock_behavior.pipe");
    }

    @Test
    public void shouldTransformAPipelineReferencingATemplate() throws IOException {
        testTransform("template_ref.pipe");
    }

    @Test
    public void shouldTransformAPipelineWhichHasADisplayOrderWeight() throws IOException {
        testTransform("display_order.pipe");
    }

    @Test
    public void shouldInverseTransformPipeline() throws IOException {
        Map<String, Object> materials = new LinkedTreeMap<>();
        materials.put("foo", new LinkedTreeMap<>());
        when(materialTransform.inverseTransform(any(LinkedTreeMap.class))).thenReturn(materials);

        testInverseTransform("export.pipe");
    }

    @Test
    public void shouldInverseTransformAPipelineWhichHasADisplayOrderWeight() throws IOException {
        Map<String, Object> materials = new LinkedTreeMap<>();
        materials.put("foo", new LinkedTreeMap<>());
        when(materialTransform.inverseTransform(any(LinkedTreeMap.class))).thenReturn(materials);

        testInverseTransform("display_order.pipe");
    }

    private void testTransform(String caseFile) throws IOException {
        testTransform(caseFile, caseFile);
    }

    private void testInverseTransform(String caseFile) throws IOException {
        testInverseTransform(caseFile, caseFile);
    }

    private void testTransform(String caseFile, String expectedFile) throws IOException {
        JsonObject expectedObject = (JsonObject) readJsonObject("parts/" + expectedFile + ".json");
        JsonObject jsonObject = parser.transform(readYamlObject("parts/" + caseFile + ".yaml"));
        assertThat(jsonObject, is(new JsonObjectMatcher(expectedObject)));
    }

    private void testInverseTransform(String caseFile, String expectedFile) throws IOException {
        String expectedObject = loadString("parts/" + expectedFile + ".yaml");
        Map<String, Object> actual = parser.inverseTransform(readJsonGson("parts/" + caseFile + ".json"));
        assertYamlEquivalent(expectedObject, YamlUtils.dump(actual));
    }
}
