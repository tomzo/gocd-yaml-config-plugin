package cd.go.plugin.config.yaml;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestUtils {

    public static JsonElement readJsonObject(String path) throws IOException {
        JsonParser parser = new JsonParser();
        return parser.parse(TestUtils.createReader(path));
    }

    public static Object readYamlObject(String path) throws IOException {
        YamlReader reader = new YamlReader(TestUtils.createReader(path));
        return reader.read();
    }

    public static InputStreamReader createReader(String path) throws IOException {
        final InputStream resourceAsStream = getResourceAsStream(path);
        return new InputStreamReader(resourceAsStream);
    }

    private static String loadString(String path) throws IOException {
        final InputStream resourceAsStream = getResourceAsStream(path);
        return IOUtils.toString(resourceAsStream);
    }

    public static InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? TestUtils.class.getResourceAsStream(resource) : in;
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
