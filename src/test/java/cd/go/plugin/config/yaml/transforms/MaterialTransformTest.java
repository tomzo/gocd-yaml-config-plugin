package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.JsonObjectMatcher;
import cd.go.plugin.config.yaml.YamlUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static cd.go.plugin.config.yaml.TestUtils.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
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
        testTransform("minimal-explicit.git", "minimal.git");
    }

    @Test
    public void shouldTransformMinimalNoUrlGit() throws IOException {
        testTransform("minimal-nourl.git", "minimal.git");
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
    public void shouldTransformMinimalConfigRepo() throws IOException {
        testTransform("minimal.cr");
    }

    @Test
    public void shouldTransformCompleteConfigRepo() throws IOException {
        testTransform("complete.cr");
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

    @Test
    public void shouldTransformCompleteP4() throws IOException {
        testTransform("complete.p4");
    }


    @Test
    public void shouldInverseTransformMinimalGit() throws IOException {
        testInverseTransform("minimal.git");
    }

    @Test
    public void shouldInverseTransformGitWhenAutoUpdateIsFalse() throws IOException {
        testInverseTransform("auto_update.git");
    }

    @Test
    public void shouldInverseTransformCompleteCr() throws IOException {
        testInverseTransform("complete.cr");
    }

    @Test
    public void shouldInverseTransformCompleteSvn() throws IOException {
        testInverseTransform("complete.svn");
    }

    @Test
    public void shouldInverseTransformCompleteGit() throws IOException {
        testInverseTransform("complete.git");
    }

    @Test
    public void shouldInverseTransformCompleteHg() throws IOException {
        testInverseTransform("complete.hg");
    }

    @Test
    public void shouldInverseTransformSimpleDependency() throws IOException {
        testInverseTransform("simple.dependency");
    }

    @Test
    public void shouldInverseTransformCompleteP4() throws IOException {
       testInverseTransform("complete.p4");
    }

    @Test
    public void shouldInverseTransformPackage() throws IOException {
        testInverseTransform("package");
    }

    @Test
    public void shouldInverseTRansformCompletePluggable() throws IOException {
        testInverseTransform("complete.pluggable");
    }

    private void testTransform(String caseFile) throws IOException {
        testTransform(caseFile, caseFile);
    }

    private void testTransform(String caseFile, String expectedFile) throws IOException {
        JsonObject expectedObject = (JsonObject) readJsonObject("parts/materials/" + expectedFile + ".json");
        JsonObject jsonObject = parser.transform(readYamlObject("parts/materials/" + caseFile + ".yaml"));
        assertThat(jsonObject, is(new JsonObjectMatcher(expectedObject)));
    }

    private void testInverseTransform(String caseFile) throws IOException {
        testInverseTransform(caseFile, caseFile);
    }

    private void testInverseTransform(String caseFile, String expectedFile) throws IOException {
        LinkedTreeMap<String, Object> actual = parser.inverseTransform(readJsonGson("parts/materials/" + caseFile + ".json"));
        JsonObject transform = parser.transform(actual);
        assertEquals((readJsonObject("parts/materials/" + expectedFile + ".json")), transform);
    }
}