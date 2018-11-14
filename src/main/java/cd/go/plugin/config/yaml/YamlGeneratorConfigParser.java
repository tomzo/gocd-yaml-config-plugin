package cd.go.plugin.config.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;

public class YamlGeneratorConfigParser {
    private static class GeneratorConfigs {
        public Map<String, GeneratorConfig> generators;
    }
    private static class GeneratorConfig {
        public String pattern;
        public String script;
    }
    public List<YamlGenerator> parseFile(File baseDir, String file) {
        try (FileInputStream inputStream = new FileInputStream(new File(baseDir, file))) {
            YamlConfig config = new YamlConfig();
            config.setAllowDuplicates(false);
            YamlReader reader = new YamlReader(new InputStreamReader(inputStream), config);
            GeneratorConfigs configs = reader.read(GeneratorConfigs.class);
            return createGenerators(configs);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    private List<YamlGenerator> createGenerators(GeneratorConfigs configs) {
        List<YamlGenerator> generators = new ArrayList<>();
        for (Map.Entry<String, GeneratorConfig>
                entry : configs.generators.entrySet()) {
            generators.add(createGenerator(entry.getKey(), entry.getValue()));
        }
        return generators;
    }
    protected YamlGenerator createGenerator(String name, GeneratorConfig config) {
        return new YamlGenerator(name, config.pattern, config.script);
    }
}
