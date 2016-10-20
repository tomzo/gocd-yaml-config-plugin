package cd.go.plugin.config.yaml;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JsonConfigCollectionTest {

    private JsonConfigCollection jsonCollection;
    private JsonObject pipe1;
    private JsonObject pipe2;
    private JsonObject devEnv;
    private JsonObject pipeInGroup;

    @Before
    public void SetUp() {
        jsonCollection = new JsonConfigCollection();

        pipe1 = new JsonObject();
        pipe1.addProperty("name", "pipe1");

        pipe2 = new JsonObject();
        pipe2.addProperty("name", "pipe2");

        pipeInGroup = new JsonObject();
        pipeInGroup.addProperty("name", "pipe3");
        pipeInGroup.addProperty("group", "mygroup");

        devEnv = new JsonObject();
        devEnv.addProperty("name", "dev");
    }

    @Test
    public void shouldReturnTargetVersion() {
        JsonObject jsonObject = jsonCollection.getJsonObject();
        assertThat(jsonObject.get("target_version") instanceof JsonPrimitive, is(true));
        assertThat(jsonObject.getAsJsonPrimitive("target_version").getAsInt(), is(1));
    }

    @Test
    public void shouldReturnEnvironmentsArrayInJsonObjectWhenEmpty() {
        JsonObject jsonObject = jsonCollection.getJsonObject();
        assertThat(jsonObject.get("environments") instanceof JsonArray, is(true));
        assertThat(jsonObject.getAsJsonArray("environments"), is(new JsonArray()));
    }

    @Test
    public void shouldAppendPipelinesToPipelinesCollection() {
        jsonCollection.addPipeline(pipe1, "pipe1.json");
        jsonCollection.addPipeline(pipe2, "pipe2.json");
        JsonObject jsonObject = jsonCollection.getJsonObject();
        assertThat(jsonObject.getAsJsonArray("pipelines").size(), is(2));
    }


    @Test
    public void shouldReturnEnvironmentsInJsonObject() {
        jsonCollection.addEnvironment(devEnv, "dev.json");
        JsonObject jsonObject = jsonCollection.getJsonObject();
        assertThat(jsonObject.getAsJsonArray("environments").size(), is(1));
    }
}