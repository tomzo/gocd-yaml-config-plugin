package cd.go.plugin.config.yaml.transforms;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonToYamlTransform {
    private final Type type = new TypeToken<HashMap<String, Map<String, Object>>>() {
    }.getType();
    private Gson gson = new Gson();
    private Yaml yaml;

    public JsonToYamlTransform() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        yaml = new Yaml(options);
    }

    public String transform(String pipelineJson) {
        HashMap<String, Map<String, Object>> d = gson.fromJson(pipelineJson, type);
        HashMap<String, Object> pipeline = transformNameToKey(d.get("pipeline"));
        return yaml.dump(pipeline);
    }

    private HashMap<String, Object> transformNameToKey(Map<String, Object> pipeline) {
        HashMap<String, Object> result = new HashMap<>();

        if (pipeline.containsKey("name")) {
            String name = (String) pipeline.get("name");
            pipeline.remove(name);
            result.put(name, transformEntries(pipeline));
        } else {
            result = transformEntries(pipeline);
        }

        return result;
    }

    private List transformList(List list) {
        List result = new ArrayList();
        for (Object v : list) {
            if (v instanceof LinkedTreeMap) {
                result.add(transformNameToKey((LinkedTreeMap) v));
            } else if (v instanceof List) {
                result.add(transformList((List) v));
            } else {
                result.add(v);
            }
        }
        return result;
    }

    private HashMap<String, Object> transformEntries(Map<String, Object> entries) {
        HashMap<String, Object> results = new HashMap<>();
        for (HashMap.Entry<String, Object> entry : entries.entrySet()) {
            if (entry.getValue() instanceof LinkedTreeMap) {
                results.put(entry.getKey(), transformNameToKey((Map<String, Object>) entry.getValue()));
            } else if (entry.getValue() instanceof List) {
                results.put(entry.getKey(), transformList((List) entry.getValue()));
            } else {
                results.put(entry.getKey(), entry.getValue());
            }
        }
        return results;
    }
}
