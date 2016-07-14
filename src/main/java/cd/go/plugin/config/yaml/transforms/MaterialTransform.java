package cd.go.plugin.config.yaml.transforms;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static cd.go.plugin.config.yaml.YamlUtils.*;

public class MaterialTransform {

    public static final String JSON_MATERIAL_TYPE_FIELD = "type";
    public static final String JSON_MATERIAL_NAME_FIELD = "name";
    public static final String JSON_MATERIAL_AUTO_UPDATE_FIELD = "auto_update";
    public static final String JSON_MATERIAL_SHALLOW_CLONE_FIELD = "shallow_clone";

    public static final String YAML_MATERIAL_TYPE_FIELD = "type";
    public static final String YAML_MATERIAL_AUTO_UPDATE_FIELD = "auto_update";
    public static final String YAML_MATERIAL_SHALLOW_CLONE_FIELD = "shallow_clone";

    public static final String YAML_SHORT_KEYWORD_GIT = "git";
    //TODO others

    public static final String YAML_BLACKLIST_KEYWORD = "blacklist";


    private final HashSet<String> yamlSpecialKeywords = new HashSet<String>();

    public MaterialTransform() {
        yamlSpecialKeywords.add(YAML_SHORT_KEYWORD_GIT);
        // TODO all other transforms
        yamlSpecialKeywords.add("type");
        yamlSpecialKeywords.add("auto_update");
        yamlSpecialKeywords.add("shallow_clone");
        yamlSpecialKeywords.add("blacklist");
        yamlSpecialKeywords.add("whitelist");
    }

    public JsonObject transform(Object maybeMaterial) {
        Map<String,Object> map = (Map<String,Object>)maybeMaterial;
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            return transform(entry);
        }
        throw new RuntimeException("expected material hash to have 1 item");
    }

    public JsonObject transform(Map.Entry<String, Object> entry) {
        String materialName = entry.getKey();
        JsonObject material = new JsonObject();
        material.addProperty(JSON_MATERIAL_NAME_FIELD, materialName);
        Map<String,Object> materialMap = (Map<String,Object>)entry.getValue();
        addOptionalString(material, materialMap, JSON_MATERIAL_TYPE_FIELD, YAML_MATERIAL_TYPE_FIELD);
        addOptionalBoolean(material, materialMap, JSON_MATERIAL_AUTO_UPDATE_FIELD, YAML_MATERIAL_AUTO_UPDATE_FIELD);
        addOptionalBoolean(material, materialMap, JSON_MATERIAL_SHALLOW_CLONE_FIELD, YAML_MATERIAL_SHALLOW_CLONE_FIELD);
        if(materialMap.containsKey("blacklist"))
            addFilter(material, materialMap.get("blacklist"), "ignore");
        if(materialMap.containsKey("whitelist"))
            addFilter(material, materialMap.get("whitelist"), "whitelist");

        String git = getOptionalString(materialMap,YAML_SHORT_KEYWORD_GIT);
        if(git != null)
        {
            material.addProperty(JSON_MATERIAL_TYPE_FIELD,YAML_SHORT_KEYWORD_GIT);
            material.addProperty("url",git);
        }
        //TODO other types

        // copy all other members
        for(Map.Entry<String, Object> materialProp : materialMap.entrySet()) {
            if(yamlSpecialKeywords.contains(materialProp.getKey()))
                continue;
            if(materialProp.getValue() instanceof String)
                material.addProperty(materialProp.getKey(),(String)materialProp.getValue());
        }
        return material;
    }

    private void addFilter(JsonObject material, Object filterList, String jsonKeyword) {
        JsonObject filter = material.getAsJsonObject("filter");
        if(filter == null) {
            filter = new JsonObject();
            material.add("filter", filter);
        }
        List<String> list = (List<String>)filterList;
        JsonArray jsonIgnores = new JsonArray();
        for(String path : list) {
            jsonIgnores.add(path);
        }
        filter.add(jsonKeyword, jsonIgnores);
    }
}
