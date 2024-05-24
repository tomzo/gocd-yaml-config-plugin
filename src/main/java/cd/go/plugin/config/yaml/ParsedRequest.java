package cd.go.plugin.config.yaml;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;

import java.util.Map;

import static java.lang.String.format;

class ParsedRequest {
    private static final Gson GSON = new Gson();
    private static final String EMPTY_REQUEST_BODY_MESSAGE = "Request body cannot be null or empty";
    private static final String INVALID_JSON = "Request body must be valid JSON string";
    private static final String MISSING_PARAM_MESSAGE = "`%s` property is missing in `%s` request";
    private static final String PARAM_NOT_A_STRING_MESSAGE = "Expected `%s` param in request type `%s` to be a string";
    private static final String PARAM_FAILED_TO_PARSE_TO_TYPE = "Failed to parse parameter `%s` for request type `%s`: %s";
    private static final String PARAM_CONFIGURATIONS = "configurations";
    private final String requestName;
    private final JsonObject params;

    private ParsedRequest(String requestName, JsonObject params) {
        this.requestName = requestName;
        this.params = params;
    }

    static ParsedRequest parse(GoPluginApiRequest req) {
        String requestBody = req.requestBody();

        if (null == requestBody || requestBody.trim().isEmpty()) {
            throw new RequestParseException(EMPTY_REQUEST_BODY_MESSAGE);
        }

        JsonElement parsed;
        try {
            parsed = JsonParser.parseString(requestBody);
        } catch (JsonParseException e) {
            throw new RequestParseException(INVALID_JSON, e);
        }

        if (parsed.equals(new JsonObject())) {
            throw new RequestParseException(EMPTY_REQUEST_BODY_MESSAGE);
        }

        try {
            return new ParsedRequest(req.requestName(), parsed.getAsJsonObject());
        } catch (Exception e) {
            throw new ParsedRequest.RequestParseException(e);
        }
    }

    String getStringParam(String paramName) {
        params.getAsJsonPrimitive(paramName);

        JsonPrimitive paramValue;
        try {
            paramValue = params.getAsJsonPrimitive(paramName);
        } catch (Exception e) {
            throw new RequestParseException(e);
        }

        if (null == paramValue) {
            throw new RequestParseException(format(MISSING_PARAM_MESSAGE, paramName, requestName));
        }

        try {
            return paramValue.getAsString();
        } catch (Exception e) {
            throw new RequestParseException(format(PARAM_NOT_A_STRING_MESSAGE, paramName, requestName));
        }
    }

    <V> Map<String, V> getParam(String paramName, Class<V> valueType) {
        try {
            JsonElement json = params.get(paramName);

            if (null == json || json.isJsonNull()) {
                throw new RequestParseException(format(MISSING_PARAM_MESSAGE, paramName, requestName));
            }

            return GSON.fromJson(json, TypeToken.getParameterized(Map.class, String.class, valueType).getType());
        } catch (Exception e) {
            throw new RequestParseException(format(PARAM_FAILED_TO_PARSE_TO_TYPE, paramName, requestName, e.getMessage()));
        }
    }

    String getConfigurationKey(String keyName) {
        String value = null;

        try {
            JsonArray perRepoConfig = params.getAsJsonArray(PARAM_CONFIGURATIONS);

            if (perRepoConfig != null) {
                for (JsonElement config : perRepoConfig) {
                    JsonObject configObj = config.getAsJsonObject();
                    String key = configObj.getAsJsonPrimitive("key").getAsString();

                    if (key.equals(keyName)) {
                        value = configObj.getAsJsonPrimitive("value").getAsString();
                    }
                }
            }
        } catch (Exception e) {
            throw new RequestParseException(e);
        }

        return value;
    }

    static class RequestParseException extends RuntimeException {
        RequestParseException(String message) {
            super(message);
        }

        RequestParseException(Throwable cause) {
            super(cause);
        }

        RequestParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
