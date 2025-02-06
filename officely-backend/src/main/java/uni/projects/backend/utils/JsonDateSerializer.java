package uni.projects.backend.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom serializer for {@link LocalDateTime} objects to JSON string format.
 */
public class JsonDateSerializer extends JsonSerializer<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.Z");

    /**
     * Serializes a {@link LocalDateTime} object to a JSON string.
     *
     * @param localDateTime the {@link LocalDateTime} object to serialize
     * @param jsonGenerator the {@link JsonGenerator} used to write the JSON
     * @param serializerProvider the {@link SerializerProvider} that can be used to get serializers for
     *                           serializing the object's properties
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        final String dateString = ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).format(formatter);
        jsonGenerator.writeString(dateString);
    }
}