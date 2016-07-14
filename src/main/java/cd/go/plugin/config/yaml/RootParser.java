package cd.go.plugin.config.yaml;

import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class RootParser {
    public JsonConfigCollection parseString(InputStreamReader yaml) throws IOException {
        YamlReader reader = new YamlReader(yaml);
        Object object = reader.read();
        return new JsonConfigCollection();
    }

}
