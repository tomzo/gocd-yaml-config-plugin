package cd.go.plugin.config.yaml;

import cd.go.plugin.config.yaml.transforms.RootTransform;
import com.beust.jcommander.internal.Maps;
import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.hubspot.jinjava.interpret.RenderResult;
import org.apache.commons.io.Charsets;
import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Scanner;

public class YamlConfigParser {
    private RootTransform rootTransform;

    public YamlConfigParser() {
        this(new RootTransform());
    }

    public YamlConfigParser(RootTransform rootTransform) {
        this.rootTransform = rootTransform;
    }

    public JsonConfigCollection parseFiles(File baseDir, String[] files) {
        JsonConfigCollection collection = new JsonConfigCollection();

        for (String file : files) {
            try {
                parseStream(collection, new FileInputStream(new File(baseDir, file)), file);
            } catch (FileNotFoundException ex) {
                collection.addError("File matching GoCD YAML pattern disappeared", file);
            }
        }

        return collection;
    }

    public void parseStream(JsonConfigCollection result, InputStream input, String location) {
        RenderResult renderedResult;

        //Jinjava requires a String as input so read the file (inputStream) into memory as a string
        Scanner s = new Scanner(input).useDelimiter("\\A");
        String template = s.hasNext() ? s.next() : "";
        if (template.isEmpty()) {
            result.addError("File is empty", location);
            return;
        }
        //Pre-Process the file through the jinja template engine
        Jinjava jinjava = new Jinjava();
        renderedResult = jinjava.renderForResult(template, Maps.newHashMap());

        //If no errors, the run the result through the YamlReader()
        if (renderedResult.getErrors().isEmpty()) {
            InputStream renderedStream = new ByteArrayInputStream(renderedResult.getOutput().getBytes(Charset.forName("UTF-8")));

            try (InputStreamReader renderedReader = new InputStreamReader(renderedStream)) {
                YamlConfig config = new YamlConfig();
                config.setAllowDuplicates(false);
                YamlReader reader = new YamlReader(renderedReader, config);
                Object rootObject = reader.read();
                JsonConfigCollection filePart = rootTransform.transform(rootObject, location);
                result.append(filePart);
            } catch (YamlReader.YamlReaderException e) {
                result.addError(e.getMessage(), location);
            } catch (IOException e) {
                result.addError(e.getMessage() + " : " + e.getCause().getMessage() + " : ", location);
            }
        } else {
            renderedResult.getErrors().forEach(e -> {
                result.addError(e.getMessage(), location);
            });
        }
    }
}
