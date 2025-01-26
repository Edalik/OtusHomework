package ru.otus.serialization.service.interfaces;

import java.io.BufferedInputStream;
import java.io.IOException;

public interface SerializationInterface<T, S> {

    void serialize(S object, Class<S> clazz) throws IOException;

    T deserialize(BufferedInputStream stream, Class<T> clazz) throws IOException;

}