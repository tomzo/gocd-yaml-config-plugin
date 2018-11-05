package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.YamlUtils;
import com.google.gson.JsonArray;
import com.google.gson.internal.LinkedTreeMap;
import org.junit.Test;

import java.io.IOException;

import static cd.go.plugin.config.yaml.TestUtils.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EnvironmentVariablesTransformTest {
    private final EnvironmentVariablesTransform parser;

    public EnvironmentVariablesTransformTest() {
        parser = new EnvironmentVariablesTransform();
    }

    @Test
    public void shouldTransformEmpty() throws IOException {
        testTransform("empty");
    }

    @Test
    public void shouldTransform1SecureVariable() throws IOException {
        testTransform("1safe");
    }

    @Test
    public void shouldTransform2Variables() throws IOException {
        testTransform("2vars");
    }

    @Test
    public void shouldInverseTransform1SecureVariable() throws IOException {
        testInverseTransform("1safe");
    }

    @Test
    public void shouldInverseTransform2Variable() throws IOException {
        testInverseTransform("2vars");
    }

    private void testTransform(String caseFile) throws IOException {
        testTransform(caseFile, caseFile);
    }

    private void testTransform(String caseFile, String expectedFile) throws IOException {
        JsonArray expected = (JsonArray) readJsonObject("parts/env_vars/" + expectedFile + ".json");
        JsonArray actual = parser.transform(readYamlObject("parts/env_vars/" + caseFile + ".yaml"));
        assertThat(actual, is(expected));
    }

    private void testInverseTransform(String caseFile) throws IOException {
        testInverseTransform(caseFile, caseFile);
    }

    private void testInverseTransform(String caseFile, String expectedFile) throws IOException {
        String expectedObject = loadString("parts/env_vars/" + expectedFile + ".yaml");
        LinkedTreeMap<String, Object> actual = parser.inverseTransform(readJsonArrayGson("parts/env_vars/" + caseFile + ".json"));
        assertYamlEquivalent(expectedObject, YamlUtils.dump(actual));
    }
}