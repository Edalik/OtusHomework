package ru.otus.serialization.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

public class NanosecondsTimestampDeserializer extends JsonDeserializer<Timestamp> {

    @Override
    public Timestamp deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        long nanoseconds = Long.parseLong(p.getText());
        long seconds = nanoseconds / 1_000_000_000;
        int nanos = (int) (nanoseconds % 1_000_000_000);
        Instant instant = Instant.ofEpochSecond(seconds, nanos);

        return Timestamp.from(instant);
    }

}