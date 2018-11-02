package cd.go.plugin.config.yaml;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TestUtils {

    public static final Type jsonType = new TypeToken<LinkedTreeMap<String, Object>>() {}.getType();

    public static JsonElement readJsonObject(String path) throws IOException {
        JsonParser parser = new JsonParser();
        return parser.parse(TestUtils.createReader(path));
    }

    public static LinkedTreeMap<String, Object> readJsonGson(String path) throws IOException {
        Gson gson = new Gson();
        return gson.fromJson(TestUtils.createReader(path), jsonType);
    }

    public static Object readYamlObject(String path) throws IOException {
        YamlConfig config = new YamlConfig();
        config.setAllowDuplicates(false);
        YamlReader reader = new YamlReader(TestUtils.createReader(path), config);
        return reader.read();
    }

    public static InputStreamReader createReader(String path) throws IOException {
        final InputStream resourceAsStream = getResourceAsStream(path);
        return new InputStreamReader(resourceAsStream);
    }

    public static String loadString(String path) throws IOException {
        final InputStream resourceAsStream = getResourceAsStream(path);
        return IOUtils.toString(resourceAsStream);
    }

    public static InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? TestUtils.class.getResourceAsStream(resource) : in;
    }

    public static void assertYamlEquivalent(String expected, String actual) {
        Yaml yaml = new Yaml();
        assertThat(yaml.load(actual), is(yaml.load(expected)));
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
