package cd.go.plugin.config.yaml.transforms;

import com.google.gson.JsonArray;
import org.junit.Test;

import java.io.IOException;

import static cd.go.plugin.config.yaml.TestUtils.readJsonObject;
import static cd.go.plugin.config.yaml.TestUtils.readYamlObject;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ParameterTransformTest {

    private final ParameterTransform paramterTransform;

    public ParameterTransformTest() {
        paramterTransform = new ParameterTransform();
    }

    @Test
    public void shouldTransformEmpty() throws IOException {
        testTransform("empty");
    }

    @Test
    public void shouldTransformAParameter() throws IOException {
        testTransform("param");
    }

    private void testTransform(String caseFile) throws IOException {
        testTransform(caseFile, caseFile);
    }

    private void testTransform(String caseFile, String expectedFile) throws IOException {
        JsonArray expected = (JsonArray) readJsonObject("parts/parameters/" + expectedFile + ".json");
        JsonArray actual = paramterTransform.transform(readYamlObject("parts/parameters/" + caseFile + ".yaml"));
        assertThat(actual, is(expected));
    }
}