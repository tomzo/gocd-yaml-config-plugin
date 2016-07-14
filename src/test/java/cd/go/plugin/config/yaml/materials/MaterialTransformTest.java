package cd.go.plugin.config.yaml.materials;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.io.IOException;

import static cd.go.plugin.config.yaml.TestUtils.readJsonObject;
import static cd.go.plugin.config.yaml.TestUtils.readYamlObject;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MaterialTransformTest {
    private final MaterialTransform parser;

    public MaterialTransformTest() {
        parser = new MaterialTransform();
    }

    @Test
    public void shouldTransformMinimalGit() throws IOException {
        testTransform("minimal.git");
    }
    @Test
    public void shouldTransformMinimalExplicitGit() throws IOException {
        testTransform("minimal-explicit.git","minimal.git");
    }
    @Test
    public void shouldTransformMinimalNoUrlGit() throws IOException {
        testTransform("minimal-nourl.git","minimal.git");
    }

    private void testTransform(String caseFile) throws IOException {
        testTransform(caseFile,caseFile);
    }

    private void testTransform(String caseFile,String expectedFile) throws IOException {
        JsonElement expectedObject = readJsonObject("parts/materials/" + expectedFile + ".json");
        JsonObject jsonObject = parser.transform(readYamlObject("parts/materials/" + caseFile + ".yaml"));
        assertThat(jsonObject,is(expectedObject));
    }

}