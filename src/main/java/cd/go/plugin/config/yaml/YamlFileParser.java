package cd.go.plugin.config.yaml;

import cd.go.plugin.config.yaml.transforms.RootTransform;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.*;

public class YamlFileParser {
    private RootTransform rootTransform;

    public YamlFileParser() {
        this.rootTransform = new RootTransform();
    }

    public YamlFileParser(RootTransform rootTransform) {
        this.rootTransform = rootTransform;
    }

    public JsonConfigCollection parseFiles(File baseDir, String[] files) {
        JsonConfigCollection collection = new JsonConfigCollection();
        for (String file : files) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(new File(baseDir, file));
                YamlReader reader = new YamlReader(new InputStreamReader(inputStream));
                Object rootObject = reader.read();
                JsonConfigCollection filePart = rootTransform.transform(rootObject, file);
                collection.append(filePart);
            } catch (FileNotFoundException ex) {
                collection.addError("File matching Go YAML pattern disappeared", file);
            } catch (IOException ex) {
                collection.addError("IO error when reading Go YAML file", file);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ex) {
                        // ignore ... any significant errors should already have been
                        // reported via an IOException
                    }
                }
            }
        }
        return collection;
    }
}
