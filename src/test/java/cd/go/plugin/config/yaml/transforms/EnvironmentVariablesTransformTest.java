package cd.go.plugin.config.yaml.transforms;

import com.google.gson.JsonArray;
import org.junit.Test;

import java.io.IOException;

import static cd.go.plugin.config.yaml.TestUtils.readJsonObject;
import static cd.go.plugin.config.yaml.TestUtils.readYamlObject;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

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

    private void testTransform(String caseFile) throws IOException {
        testTransform(caseFile,caseFile);
    }

    private void testTransform(String caseFile,String expectedFile) throws IOException {
        JsonArray expected = (JsonArray)readJsonObject("parts/env_vars/" + expectedFile + ".json");
        JsonArray actual = parser.transform(readYamlObject("parts/env_vars/" + caseFile + ".yaml"));
        assertThat(actual,is(expected));
    }
}