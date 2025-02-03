package ru.otus.serialization.service.binary_serialization;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.serialization.exception.BusinessException;
import ru.otus.serialization.service.interfaces.SerializationInterface;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static ru.otus.serialization.config.SerializationConfig.OUTPUT_PATH;

@Slf4j
@Service(JavaSerializationService.SERVICE_NAME)
public class JavaSerializationService<T, S extends Serializable> implements SerializationInterface<T, S> {

    private static final String DESERIALIZATION_ERROR = "Error deserializing using Java serialization";

    public static final String SERVICE_NAME = "java";

    @Override
    public void serialize(S object, Class<S> clazz) throws IOException {
        String path = String.format("%s/output.java", OUTPUT_PATH);
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(path)))) {
            objectOutputStream.writeObject(object);
        }

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path)))) {
            S deserialized = (S) objectInputStream.readObject();
            System.out.println(deserialized);
        } catch (ClassNotFoundException e) {
            log.error(DESERIALIZATION_ERROR, e);
            throw new BusinessException(DESERIALIZATION_ERROR);
        }
    }

    @Override
    public T deserialize(BufferedInputStream stream, Class<T> clazz) throws IOException {
        T result;
        try {
            Object readObject = new ObjectInputStream(stream).readObject();
            result = (T) readObject;
        } catch (ClassNotFoundException e) {
            log.error(DESERIALIZATION_ERROR, e);
            throw new BusinessException(DESERIALIZATION_ERROR);
        }
        return result;
    }

}