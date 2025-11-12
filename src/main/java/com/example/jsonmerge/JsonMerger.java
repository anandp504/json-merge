package com.example.jsonmerge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;

public final class JsonMerger {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonMerger() {
    }

    public static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
        if (mainNode == null || mainNode.isNull()) {
            return updateNode;
        }
        if (updateNode == null || updateNode.isNull()) {
            return mainNode;
        }

        if (mainNode.isObject() && updateNode.isObject()) {
            ObjectNode result = ((ObjectNode) mainNode).deepCopy();
            updateNode.fieldNames().forEachRemaining(fieldName -> {
                JsonNode valueToUpdate = updateNode.get(fieldName);
                JsonNode existingValue = result.get(fieldName);

                if (existingValue != null && existingValue.isObject() && valueToUpdate.isObject()) {
                    result.set(fieldName, merge(existingValue, valueToUpdate));
                } else {
                    result.set(fieldName, valueToUpdate);
                }
            });
            return result;
        }

        return updateNode;
    }

    public static JsonNode merge(String sourceJson, String updateJson) throws IOException {
        JsonNode mainNode = sourceJson == null ? null : MAPPER.readTree(sourceJson);
        JsonNode updateNode = updateJson == null ? null : MAPPER.readTree(updateJson);
        return merge(mainNode, updateNode);
    }

    public static String mergeToString(String mainJson, String updateJson) throws IOException {
        JsonNode merged = merge(mainJson, updateJson);
        return merged == null ? null : MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(merged);
    }
}
