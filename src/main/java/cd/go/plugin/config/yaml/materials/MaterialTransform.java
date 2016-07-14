package cd.go.plugin.config.yaml.materials;

import cd.go.plugin.config.yaml.YamlConfigException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static cd.go.plugin.config.yaml.YamlUtils.getOptionalBoolean;
import static cd.go.plugin.config.yaml.YamlUtils.getOptionalString;

public class MaterialTransform {

    public static final String JSON_MATERIAL_TYPE_FIELD = "type";
    public static final String JSON_MATERIAL_NAME_FIELD = "name";
    public static final String JSON_MATERIAL_AUTO_UPDATE_FIELD = "auto_update";

    public static final String YAML_SHORT_KEYWORD_GIT = "git";
    //TODO others

    public static final String YAML_BLACKLIST_KEYWORD = "blacklist";


    private final HashSet<String> yamlSpecialKeywords = new HashSet<String>();

    public MaterialTransform() {
        yamlSpecialKeywords.add(YAML_SHORT_KEYWORD_GIT);
        // TODO all other materials
        yamlSpecialKeywords.add("type");
        yamlSpecialKeywords.add("auto_update");
        yamlSpecialKeywords.add("blacklist");
        yamlSpecialKeywords.add("whitelist");
    }

    public JsonObject transform(Object maybeMaterial) {
        Map<String,Object> map = (Map<String,Object>)maybeMaterial;
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            String materialName = entry.getKey();
            JsonObject material = new JsonObject();
            material.addProperty(JSON_MATERIAL_NAME_FIELD, materialName);
            Map<String,Object> materialMap = (Map<String,Object>)entry.getValue();
            String materialType = getOptionalString(materialMap,"type");
            if(materialType != null)
                material.addProperty(JSON_MATERIAL_TYPE_FIELD,materialType);
            Boolean autoUpdate = getOptionalBoolean(materialMap,"auto_update");
            if(autoUpdate != null)
                material.addProperty(JSON_MATERIAL_AUTO_UPDATE_FIELD,autoUpdate);
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
        throw new RuntimeException("expected material hash to have 1 item");
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
