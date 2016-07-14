package cd.go.plugin.config.yaml.materials;

import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Map;

public class MaterialTransform {

    public static final String JSON_MATERIAL_TYPE_FIELD = "type";
    public static final String JSON_MATERIAL_NAME_FIELD = "name";

    public static final String YAML_SHORT_KEYWORD_GIT = "git";
    private final HashSet<String> yamlShortKeywords = new HashSet<String>();

    public MaterialTransform() {
        yamlShortKeywords.add(YAML_SHORT_KEYWORD_GIT);
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

            String git = getOptionalString(materialMap,YAML_SHORT_KEYWORD_GIT);
            if(git != null)
            {
                material.addProperty(JSON_MATERIAL_TYPE_FIELD,YAML_SHORT_KEYWORD_GIT);
                material.addProperty("url",git);
            }

            for(Map.Entry<String, Object> materialProp : materialMap.entrySet()) {
                if(yamlShortKeywords.contains(materialProp.getKey()))
                    continue;
                if(materialProp.getValue() instanceof String)
                    material.addProperty(materialProp.getKey(),(String)materialProp.getValue());
            }
            return material;
        }
        throw new RuntimeException("expected material hash to have 1 item");
    }

    private String getOptionalString(Map map, String fieldName) {
        Object type = map.get(fieldName);
        if(type != null)
        {
            return (String)type;
        }
        return null;
    }
}
