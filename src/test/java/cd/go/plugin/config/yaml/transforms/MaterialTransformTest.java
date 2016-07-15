package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.JsonObjectMatcher;
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

    @Test
    public void shouldTransformGitWhenAutoUpdateIsFalse() throws IOException {
        testTransform("auto_update.git");
    }

    @Test
    public void shouldTransformCompleteGit() throws IOException {
        testTransform("complete.git");
    }

    @Test
    public void shouldTransformCompleteSvn() throws IOException {
        testTransform("complete.svn");
    }

    @Test
    public void shouldTransformCompleteHg() throws IOException {
        testTransform("complete.hg");
    }

    @Test
    public void shouldTransformSimpleDependency() throws IOException {
        testTransform("simple.dependency");
    }

    @Test
    public void shouldTransformCompletePluggable() throws IOException {
        testTransform("complete.pluggable");
    }

    @Test
    public void shouldTransformPackage() throws IOException {
        testTransform("package");
    }

    private void testTransform(String caseFile) throws IOException {
        testTransform(caseFile,caseFile);
    }

    private void testTransform(String caseFile,String expectedFile) throws IOException {
        JsonObject expectedObject = (JsonObject)readJsonObject("parts/materials/" + expectedFile + ".json");
        JsonObject jsonObject = parser.transform(readYamlObject("parts/materials/" + caseFile + ".yaml"));
        assertThat(jsonObject,is(new JsonObjectMatcher(expectedObject)));
    }

}