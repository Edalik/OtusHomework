package ru.otus.serialization.service.text_serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.otus.serialization.service.interfaces.SerializationInterface;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static ru.otus.serialization.config.SerializationConfig.OUTPUT_PATH;

public abstract class AbstractTextSerializationService<T, S> implements SerializationInterface<T, S> {

    @Override
    public void serialize(S object, Class<S> clazz) throws IOException {
        String path = String.format("%s/output.%s", OUTPUT_PATH, getExtension());
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path))) {
            stream.write(getMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(object));
        }

        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(path))) {
            S o = getMapper().readValue(stream, clazz);
            System.out.println(getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o));
        }
    }

    @Override
    public T deserialize(BufferedInputStream stream, Class<T> clazz) throws IOException {
        return getMapper().readValue(stream, clazz);
    }

    protected ObjectMapper getMapper() {
        return null;
    }

    protected String getExtension() {
        return null;
    }

}