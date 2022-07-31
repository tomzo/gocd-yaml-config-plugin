package cd.go.plugin.config.yaml.transforms;

import com.google.gson.JsonArray;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static cd.go.plugin.config.yaml.TestUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    public void shouldInverseTransformAParameter() throws IOException {
        testInverseTransform("param");
    }

    private void testTransform(String caseFile) throws IOException {
        testTransform(caseFile, caseFile);
    }

    private void testTransform(String caseFile, String expectedFile) throws IOException {
        JsonArray expected = (JsonArray) readJsonObject("parts/parameters/" + expectedFile + ".json");
        JsonArray actual = paramterTransform.transform(readYamlObject("parts/parameters/" + caseFile + ".yaml"));
        assertThat(actual, is(expected));
    }

    private void testInverseTransform(String caseFile) throws IOException {
        testInverseTransform(caseFile, caseFile);
    }

    private void testInverseTransform(String caseFile, String expectedFile) throws IOException {
        Map<String, Object> inverse = paramterTransform.inverseTransform(readJsonArrayGson("parts/parameters/" + caseFile + ".json"));
        JsonArray actual = paramterTransform.transform(inverse);
        assertEquals((readJsonObject("parts/parameters/" + expectedFile + ".json")), actual);
    }

}