package cd.go.plugin.config.yaml;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Map;

public class JsonObjectMatcher extends TypeSafeMatcher<JsonObject> {
    private final JsonObject expected;

    public JsonObjectMatcher(JsonObject expected) {
        this.expected = expected;
    }
    @Override
    protected boolean matchesSafely(JsonObject actual) {
        if(actual == null)
            return false;
        for(Map.Entry<String,JsonElement> field : this.expected.entrySet()){
            if(!actual.has(field.getKey()))
                return false;
            if(field.getValue().isJsonObject())
            {
                JsonObjectMatcher inner = new JsonObjectMatcher(field.getValue().getAsJsonObject());
                JsonObject otherObj = actual.get(field.getKey()).getAsJsonObject();
                if(!inner.matchesSafely(otherObj))
                    return false;
            }
            else if(field.getValue().isJsonArray()){
                JsonArray thisArray = field.getValue().getAsJsonArray();
                JsonArray otherArray = actual.get(field.getKey()).getAsJsonArray();
                if(otherArray == null)
                    return false;
                if(thisArray.size() != otherArray.size())
                    return false;
                for(int i = 0; i < thisArray.size(); i++){
                    JsonElement thisItem = thisArray.get(i);
                    if(!equalToAnyOther(thisItem,otherArray))
                        return false;
                }
            }
            else if(!field.getValue().equals(actual.get(field.getKey())))
                return false;
        }
        return true;
    }

    private boolean equalToAnyOther(JsonElement thisItem, JsonArray otherArray) {
        for(int i = 0; i < otherArray.size();i++) {
            JsonElement otherItem = otherArray.get(i);
            if (thisItem.isJsonObject()) {
                if (!otherItem.isJsonObject())
                    continue;
                if (new JsonObjectMatcher(thisItem.getAsJsonObject()).matchesSafely(otherItem.getAsJsonObject()))
                    return true;
            } else if (thisItem.equals(otherItem))
                return true;
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(expected.toString());
    }
}
