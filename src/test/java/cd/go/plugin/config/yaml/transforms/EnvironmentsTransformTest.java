package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.JsonObjectMatcher;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static cd.go.plugin.config.yaml.TestUtils.readJsonObject;
import static cd.go.plugin.config.yaml.TestUtils.readYamlObject;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EnvironmentsTransformTest {
    private final EnvironmentsTransform parser;

    public EnvironmentsTransformTest() {
        parser = new EnvironmentsTransform(new EnvironmentVariablesTransform());
    }

    @Test
    public void shouldTransformCompleteEnvironment() throws IOException {
        testTransform("complete");
    }

    @Test
    public void shouldTransformEmptyEnvironment() throws IOException {
        testTransform("empty");
    }

    private JsonObject testTransform(String caseFile) throws IOException {
        return testTransform(caseFile, caseFile);
    }

    private JsonObject testTransform(String caseFile, String expectedFile) throws IOException {
        JsonObject expectedObject = (JsonObject) readJsonObject("parts/environments/" + expectedFile + ".json");
        JsonObject jsonObject = parser.transform(readYamlObject("parts/environments/" + caseFile + ".yaml"));
        assertThat(jsonObject, is(new JsonObjectMatcher(expectedObject)));
        return jsonObject;
    }
}