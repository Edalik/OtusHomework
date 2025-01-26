package ru.otus.serialization.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

public class NanosecondsTimestampSerializer extends JsonSerializer<Timestamp> {

    @Override
    public void serialize(Timestamp value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        Instant instant = value.toInstant();
        long nanoseconds = instant.getEpochSecond() * 1_000_000_000 + instant.getNano();
        gen.writeNumber(nanoseconds);
    }

}