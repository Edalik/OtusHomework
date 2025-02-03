package ru.otus.serialization.service.interfaces;

import ru.otus.serialization.enums.SerializationType;

import java.io.IOException;

public interface SerializationService {

    Object serialization(SerializationType serializationType) throws IOException;

}