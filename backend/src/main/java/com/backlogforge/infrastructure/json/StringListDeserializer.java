package com.backlogforge.infrastructure.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Deserializador resiliente para campos List<String> (como suggestedTechnologies).
 * Aceita arrays planos de strings ["Java", "React"], objetos {"frontend": "React", "backend": "Java"}
 * ou strings delimitadas por vírgula ("Java, React").
 */
public class StringListDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = p.currentToken();
        List<String> result = new ArrayList<>();

        if (currentToken == JsonToken.START_ARRAY) {
            JsonNode node = p.getCodec().readTree(p);
            if (node.isArray()) {
                for (JsonNode item : node) {
                    if (item.isTextual()) {
                        result.add(item.asText());
                    } else if (item.isObject()) {
                        extractStringsFromNode(item, result);
                    } else {
                        result.add(item.asText());
                    }
                }
            }
        } else if (currentToken == JsonToken.START_OBJECT) {
            JsonNode node = p.getCodec().readTree(p);
            extractStringsFromNode(node, result);
        } else if (currentToken == JsonToken.VALUE_STRING) {
            String text = p.getText();
            if (text != null && !text.isBlank()) {
                for (String part : text.split("[,;]")) {
                    if (!part.isBlank()) {
                        result.add(part.trim());
                    }
                }
            }
        }

        return result;
    }

    private void extractStringsFromNode(JsonNode node, List<String> result) {
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                JsonNode val = entry.getValue();
                if (val.isArray()) {
                    for (JsonNode elem : val) {
                        result.add(elem.asText());
                    }
                } else if (val.isObject()) {
                    extractStringsFromNode(val, result);
                } else if (val.isValueNode()) {
                    result.add(val.asText());
                }
            });
        }
    }
}
