package uni.projects.backend.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Custom deserializer for {@link LocalDateTime} objects from JSON string format.
 */
public class JsonDateDeserializer extends JsonDeserializer<LocalDateTime> {

    /**
     * Deserializes a JSON string to a {@link LocalDateTime} object.
     *
     * @param jp the {@link JsonParser} used to parse the JSON
     * @param ctxt the {@link DeserializationContext} that can be used to access information about the deserialization process
     * @return the deserialized {@link LocalDateTime} object
     * @throws IOException if an I/O error occurs
     */
    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jp.getCodec();
        TextNode node = oc.readTree(jp);
        String dateString = node.textValue();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return LocalDateTime.parse(dateString, formatter)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}