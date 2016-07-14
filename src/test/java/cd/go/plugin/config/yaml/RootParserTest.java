package cd.go.plugin.config.yaml;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static org.junit.Assert.*;

public class RootParserTest {
    @Test
    public void shouldReadSimpleFile() throws IOException {
        YamlReader reader = new YamlReader(TestUtils.createReader("examples/simple.gocd.yaml"));
        Object object = reader.read();
    }
}