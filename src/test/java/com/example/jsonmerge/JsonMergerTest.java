package com.example.jsonmerge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class JsonMergerTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void mergesNestedFieldsWithoutLosingOriginalValues() throws Exception {
        String originalJson = """
            {
              \"user\": { \"name\": \"Alice\", \"age\": 25 },
              \"active\": true
            }
            """;

        String updateJson = """
            {
              \"user\": { \"age\": 26, \"email\": \"alice@example.com\" },
              \"active\": false,
              \"role\": \"admin\"
            }
            """;

        JsonNode merged = JsonMerger.merge(MAPPER.readTree(originalJson), MAPPER.readTree(updateJson));

        assertNotNull(merged);
        assertEquals("Alice", merged.path("user").path("name").asText());
        assertEquals(26, merged.path("user").path("age").asInt());
        assertEquals("alice@example.com", merged.path("user").path("email").asText());
        assertFalse(merged.path("active").asBoolean());
        assertEquals("admin", merged.path("role").asText());
    }

    @Test
    void returnsUpdateWhenOriginalIsNull() throws Exception {
        String updateJson = """
            {
              \"status\": \"updated\"
            }
            """;

        JsonNode merged = JsonMerger.merge(null, MAPPER.readTree(updateJson));

        assertNotNull(merged);
        assertEquals("updated", merged.path("status").asText());
    }

    @Test
    void returnsOriginalWhenUpdateIsNull() throws Exception {
        String originalJson = """
            {
              \"status\": \"original\"
            }
            """;

        JsonNode merged = JsonMerger.merge(MAPPER.readTree(originalJson), null);

        assertNotNull(merged);
        assertEquals("original", merged.path("status").asText());
    }

    @Test
    void mergeStringsConvenienceMethodProducesPrettyJson() throws Exception {
        String originalJson = """
            {
              \"flag\": true
            }
            """;

        String updateJson = """
            {
              \"flag\": false,
              \"count\": 3
            }
            """;

        String mergedJson = JsonMerger.mergeToString(originalJson, updateJson);

        assertNotNull(mergedJson);
        JsonNode merged = MAPPER.readTree(mergedJson);
        assertFalse(merged.path("flag").asBoolean());
        assertEquals(3, merged.path("count").asInt());
    }

    @Test
    void mergeHandlesBothNull() throws Exception {
        JsonNode merged = JsonMerger.merge((JsonNode) null, null);
        assertNull(merged);
    }
}
