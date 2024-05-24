package cd.go.plugin.config.yaml;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {
    private static final Gson GSON = new Gson();

    public static JsonElement readJsonObject(String path) {
        return JsonParser.parseReader(createReader(path));
    }

    public static List<Map<String, Object>> readJsonArrayGson(String path) {
        return GSON.fromJson(createReader(path), new TypeToken<List<Map<String, Object>>>() {}.getType());
    }

    public static Map<String, Object> readJsonGson(String path) {
        return GSON.fromJson(createReader(path), new TypeToken<Map<String, Object>>() {}.getType());
    }

    public static Object readYamlObject(String path) throws IOException {
        try (YamlReader reader = new YamlReader(createReader(path), YamlConfigParser.newYamlConfig())) {
            return reader.read();
        }
    }

    private static InputStreamReader createReader(String path) {
        return new InputStreamReader(getResourceAsStream(path));
    }

    public static String loadString(String path) throws IOException {
        try (InputStream resourceAsStream = getResourceAsStream(path)) {
            return new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @SuppressWarnings("resource")
    static InputStream getResourceAsStream(String resource) {
        final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);

        return in == null ? TestUtils.class.getResourceAsStream(resource) : in;
    }

    public static void assertYamlEquivalent(String expected, String actual) {
        Yaml yaml = new Yaml();
        Object expectedParse = yaml.load(expected);
        Object actualParse = yaml.load(actual);
        assertEquals(expectedParse, actualParse);
    }

}
