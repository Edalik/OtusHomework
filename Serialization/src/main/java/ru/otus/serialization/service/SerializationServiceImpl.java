package ru.otus.serialization.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import ru.otus.serialization.enums.SerializationType;
import ru.otus.serialization.model.ProcessedSMS;
import ru.otus.serialization.model.SMS;
import ru.otus.serialization.service.interfaces.SerializationInterface;
import ru.otus.serialization.service.interfaces.SerializationService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SerializationServiceImpl implements SerializationService {

    private final Map<String, SerializationInterface<SMS, ProcessedSMS>> serializationServices;

    public Object serialization(SerializationType serializationType) throws IOException {
        SerializationInterface<SMS, ProcessedSMS> serializationService = serializationServices.getOrDefault(serializationType.getExtension(), null);
        if (serializationService == null) {
            throw new IllegalArgumentException("Unexpected value: " + serializationType);
        }

        File file = ResourceUtils.getFile(String.format("classpath:input/sms.%s", serializationType.getExtension()));
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))) {
            return serializationService.deserialize(stream, SMS.class);
        }
    }

}