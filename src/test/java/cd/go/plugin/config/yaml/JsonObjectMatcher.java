package cd.go.plugin.config.yaml;

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
    protected boolean matchesSafely(JsonObject item) {
        for(Map.Entry<String,JsonElement> field : this.expected.entrySet()){
            if(!item.has(field.getKey()))
                return false;
            if(!field.getValue().equals(item.get(field.getKey())))//TODO replace by another matcher
                return false;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(expected.toString());
    }
}
