package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.JsonConfigCollection;
import cd.go.plugin.config.yaml.YamlConfigException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static cd.go.plugin.config.yaml.TestUtils.readYamlObject;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class RootTransformTest {
    private RootTransform rootTransform;
    private PipelineTransform pipelineTransform;
    private EnvironmentsTransform environmentsTransform;
    private TemplateTransform templateTransform;

    @Before
    public void Setup() {
        pipelineTransform = mock(PipelineTransform.class);
        environmentsTransform = mock(EnvironmentsTransform.class);
        templateTransform = mock(TemplateTransform.class);
        rootTransform = new RootTransform(pipelineTransform, environmentsTransform, templateTransform);
    }

    @Test
    public void shouldTransformEmptyRoot() throws IOException {
        JsonConfigCollection empty = readRootYaml("empty");
        assertThat(empty.getJsonObject().isJsonObject(), is(true));
        assertThat(empty.getJsonObject().get("environments").isJsonArray(), is(true));
        assertThat(empty.getJsonObject().get("pipelines").isJsonArray(), is(true));
        assertThat(empty.getJsonObject().get("environments").getAsJsonArray().size(), is(0));
        assertThat(empty.getJsonObject().get("pipelines").getAsJsonArray().size(), is(0));
        assertThat(empty.getJsonObject().get("templates").getAsJsonArray().size(), is(0));
    }

    @Test
    public void shouldTransformRootWhen2PipelinesAnd2Environments() throws IOException {
        JsonConfigCollection collection = readRootYaml("2");
        assertThat(collection.getJsonObject().get("environments").getAsJsonArray().size(), is(2));
        assertThat(collection.getJsonObject().get("pipelines").getAsJsonArray().size(), is(2));
    }

    @Test
    public void shouldTransformRootWithCommonSection() throws IOException {
        JsonConfigCollection collection = readRootYaml("common_section");
        assertThat(collection.getJsonObject().get("environments").getAsJsonArray().size(), is(1));
        assertThat(collection.getJsonObject().get("pipelines").getAsJsonArray().size(), is(1));
    }

    @Test
    public void shouldRecognizeAndUpdateVersion() throws Exception {
        assertTargetVersion(readRootYaml("version_not_present").getJsonObject(), 1);
        assertTargetVersion(readRootYaml("version_1").getJsonObject(), 1);
        assertTargetVersion(readRootYaml("version_2").getJsonObject(), 2);
    }
    
    @Test
    public void shouldPreserveOrderOfPipelines() throws IOException {
        MaterialTransform materialTransform = mock(MaterialTransform.class);
        StageTransform stageTransform = mock(StageTransform.class);
        EnvironmentVariablesTransform environmentTransform = mock(EnvironmentVariablesTransform.class);
        ParameterTransform parameterTransform = mock(ParameterTransform.class);
        TemplateTransform templateTransform = mock(TemplateTransform.class);
        pipelineTransform = new PipelineTransform(materialTransform, stageTransform, environmentTransform, parameterTransform);
        rootTransform = new RootTransform(pipelineTransform, environmentsTransform, templateTransform);
        
        JsonConfigCollection collection = readRootYaml("pipeline_order");
        JsonArray pipelines = collection.getJsonObject().get("pipelines").getAsJsonArray();
        assertThat(pipelines.get(0).getAsJsonObject().get("name").getAsString(), is("pipe1"));
        assertThat(pipelines.get(1).getAsJsonObject().get("name").getAsString(), is("pipe2"));
        assertThat(pipelines.get(2).getAsJsonObject().get("name").getAsString(), is("pipe3"));
    }

    @Test(expected = YamlReader.YamlReaderException.class)
    public void shouldNotTransformRootWhenYAMLHasDuplicateKeys() throws IOException {
        readRootYaml("duplicate.materials.pipe");
        fail("should have thrown duplicate keys error");
    }

    private JsonConfigCollection readRootYaml(String caseFile) throws IOException {
        return rootTransform.transform(readYamlObject("parts/roots/" + caseFile + ".yaml"), "test code");
    }

    private void assertTargetVersion(JsonObject jsonObject, int expectedVersion) {
        assertThat(jsonObject.get("target_version") instanceof JsonPrimitive, is(true));
        assertThat(jsonObject.getAsJsonPrimitive("target_version").getAsInt(), is(expectedVersion));
    }
}