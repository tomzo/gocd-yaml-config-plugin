package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.JsonObjectMatcher;
import cd.go.plugin.config.yaml.YamlUtils;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static cd.go.plugin.config.yaml.TestUtils.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class MaterialTransformTest {
    private final MaterialTransform parser;
    // Becomes relevant only when testing whitelist/includes migration
    private final int defaultVersionForContext = 10;

    public MaterialTransformTest() {
        parser = new MaterialTransform();
    }

    @Test
    public void shouldTransformMinimalGit() throws IOException {
        testTransform("minimal.git", defaultVersionForContext);
    }

    @Test
    public void shouldTransformMinimalExplicitGit() throws IOException {
        testTransform("minimal-explicit.git", "minimal.git", defaultVersionForContext);
    }

    @Test
    public void shouldTransformMinimalNoUrlGit() throws IOException {
        testTransform("minimal-nourl.git", "minimal.git", defaultVersionForContext);
    }

    @Test
    public void shouldTransformGitWhenAutoUpdateIsFalse() throws IOException {
        testTransform("auto_update.git", defaultVersionForContext);
    }

    @Test
    public void shouldTransformGitWhenPlainPassword() throws IOException {
        testTransform("password.git", defaultVersionForContext);
    }

    @Test
    public void shouldTransformCompleteGitWhenFormat9() throws IOException {
        testTransform("complete9.git", 9);
    }

    @Test
    public void shouldTransformCompleteGitWhenFormat10() throws IOException {
        testTransform("complete10.git", defaultVersionForContext);
    }

    @Test
    public void shouldTransformCompleteGitWhenFormat10BackwardsComp() throws IOException {
        testTransform("complete10backwards.git", defaultVersionForContext);
    }

    @Test
    public void shouldTransformMinimalConfigRepo() throws IOException {
        testTransform("minimal.cr", defaultVersionForContext);
    }

    @Test
    public void shouldTransformCompleteConfigRepo() throws IOException {
        testTransform("complete10.cr", defaultVersionForContext);
    }

    @Test
    public void shouldTransformCompleteSvn() throws IOException {
        testTransform("complete10.svn", defaultVersionForContext);
    }

    @Test
    public void shouldTransformCompleteHg() throws IOException {
        testTransform("complete10.hg", defaultVersionForContext);
    }

    @Test
    public void shouldTransformHgWhenPlainPassword() throws IOException {
        testTransform("password.hg", defaultVersionForContext);
    }

    @Test
    public void shouldTransformHgWhenBranchIsPresent() throws IOException {
        testTransform("branch.hg", defaultVersionForContext);
    }

    @Test
    public void shouldTransformSimpleDependency() throws IOException {
        testTransform("simple.dependency", defaultVersionForContext);
    }

    @Test
    public void shouldTransformDependencyWithIgnoreForSchedulingFlag() throws IOException {
        testTransform("ignore_for_scheduling.dependency", defaultVersionForContext);
    }

    @Test
    public void shouldTransformCompletePluggable() throws IOException {
        testTransform("complete10.pluggable", defaultVersionForContext);
    }

    @Test
    public void shouldTransformCompleteSCM() throws IOException {
        testTransform("complete10.scm", defaultVersionForContext);
    }

    @Test
    public void shouldInverseTransformCompleteSCM() throws IOException {
        testInverseTransform("complete10.scm");
    }

    @Test
    public void shouldTransformPackage() throws IOException {
        testTransform("package", defaultVersionForContext);
    }

    @Test
    public void shouldTransformCompleteP4() throws IOException {
        testTransform("complete10.p4", defaultVersionForContext);
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
        testInverseTransform("complete10.cr");
    }

    @Test
    public void shouldInverseTransformCompleteSvn() throws IOException {
        testInverseTransform("complete10.svn");
    }

    @Test
    public void shouldInverseTransformCompleteGit() throws IOException {
        testInverseTransform("complete10.git");
    }

    @Test
    public void shouldInverseTransformCompleteHg() throws IOException {
        testInverseTransform("complete10.hg");
    }

    @Test
    public void shouldInverseTransformSimpleDependency() throws IOException {
        testInverseTransform("simple.dependency");
    }

    @Test
    public void shouldInverseTransformDependencyWithIgnoreForSchedulingFlag() throws IOException {
        testInverseTransform("ignore_for_scheduling.dependency");
    }

    @Test
    public void shouldInverseTransformCompleteP4() throws IOException {
        testInverseTransform("complete10.p4");
    }

    @Test
    public void shouldInverseTransformPackage() throws IOException {
        testInverseTransform("package");
    }

    @Test
    public void shouldInverseTRansformCompletePluggable() throws IOException {
        testInverseTransform("complete10.pluggable");
    }

    @Test
    public void inverseTransform_shouldGenerateARandomMaterialNameInAbsenceOfName() {
        Map<String, Object> material = parser.inverseTransform(readJsonGson("parts/materials/material_without_name.git.json"));

        String materialName = material.keySet().stream().findFirst().get();
        assertThat(materialName, containsString("git-"));
    }

    private void testTransform(String caseFile, int formatVersion) throws IOException {
        testTransform(caseFile, caseFile, formatVersion);
    }

    private void testTransform(String caseFile, String expectedFile, int formatVersion) throws IOException {
        JsonObject expectedObject = (JsonObject) readJsonObject("parts/materials/" + expectedFile + ".json");
        JsonObject jsonObject = parser.transform(readYamlObject("parts/materials/" + caseFile + ".yaml"), formatVersion);
        assertThat(jsonObject, is(new JsonObjectMatcher(expectedObject)));
    }

    private void testInverseTransform(String caseFile) throws IOException {
        testInverseTransform(caseFile, caseFile);
    }

    private void testInverseTransform(String caseFile, String expectedFile) throws IOException {
        String expectedObject = loadString("parts/materials/" + expectedFile + ".yaml");
        Map<String, Object> actual = parser.inverseTransform(readJsonGson("parts/materials/" + caseFile + ".json"));
        assertYamlEquivalent(expectedObject, YamlUtils.dump(actual));
    }
}
