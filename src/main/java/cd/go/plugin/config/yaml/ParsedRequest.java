package cd.go.plugin.config.yaml;

import com.google.gson.*;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;

import static java.lang.String.format;

class ParsedRequest {
    private static final String EMPTY_REQUEST_BODY_MESSAGE = "Request body cannot be null or empty";
    private static final String INVALID_JSON = "Request body must be valid JSON string";
    private static final String MISSING_PARAM_MESSAGE = "`%s` property is missing in `%s` request";
    private static final String PARAM_NOT_A_STRING_MESSAGE = "Expected `%s` param in request type `%s` to be a string";
    private static final String PARAM_CONFIGURATIONS = "configurations";
    private static final String INVALID_REPO_CONFIGURATION_KEY = "Config repo configuration has invalid key `%s`";

    private final String requestName;
    private final JsonObject params;

    private ParsedRequest(String requestName, JsonObject params) {
        this.requestName = requestName;
        this.params = params;
    }

    static ParsedRequest parse(GoPluginApiRequest req) {
        JsonParser parser = new JsonParser();
        String requestBody = req.requestBody();

        if (null == requestBody || requestBody.trim().isEmpty()) {
            throw new RequestParseException(EMPTY_REQUEST_BODY_MESSAGE);
        }

        JsonElement parsed;
        try {
            parsed = parser.parse(requestBody);
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
                    } else {
                        throw new RequestParseException(format(INVALID_REPO_CONFIGURATION_KEY, key));
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
