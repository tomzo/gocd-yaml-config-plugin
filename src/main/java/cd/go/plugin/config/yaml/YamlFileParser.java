package cd.go.plugin.config.yaml;

import cd.go.plugin.config.yaml.transforms.RootTransform;
import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.*;

public class YamlFileParser {
    private RootTransform rootTransform;

    public YamlFileParser() {
        this(new RootTransform());
    }

    public YamlFileParser(RootTransform rootTransform) {
        this.rootTransform = rootTransform;
    }

    public JsonConfigCollection parseFiles(File baseDir, String[] files) {
        JsonConfigCollection collection = new JsonConfigCollection();

        for (String file : files) {
            try (InputStream inputStream = new FileInputStream(new File(baseDir, file))) {
                YamlConfig config = new YamlConfig();
                config.setAllowDuplicates(false);
                YamlReader reader = new YamlReader(new InputStreamReader(inputStream), config);
                Object rootObject = reader.read();
                JsonConfigCollection filePart = rootTransform.transform(rootObject, file);
                collection.append(filePart);
            } catch (YamlReader.YamlReaderException ex) {
                collection.addError(ex.getMessage(), file);
            } catch (FileNotFoundException ex) {
                collection.addError("File matching GoCD YAML pattern disappeared", file);
            } catch (IOException ex) {
                collection.addError(ex.getMessage() + " : " + ex.getCause().getMessage() + " : ", file);
            }
        }

        return collection;
    }
}
