package com.example.jsonmerge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class App {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private App() {
    }

    public static void main(String[] args) throws Exception {
        try (InputStream originalStream = App.class.getResourceAsStream("/original_item.json");
             InputStream updateStream = App.class.getResourceAsStream("/update_item.json")) {

            Objects.requireNonNull(originalStream, "Resource original_item.json not found");
            Objects.requireNonNull(updateStream, "Resource update_item.json not found");

            JsonNode merged = JsonMerger.merge(
                    MAPPER.readTree(originalStream),
                    MAPPER.readTree(updateStream)
            );
            Path outputPath = Path.of("src/main/resources/merged_item.json");
            Files.createDirectories(outputPath.getParent());
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(outputPath.toFile(), merged);
            System.out.println("Merged JSON written to " + outputPath.toAbsolutePath());
        }
    }
}
