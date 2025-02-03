package ru.otus.serialization.service.text_serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service(JsonSerializationService.SERVICE_NAME)
public class JsonSerializationService<T, S> extends AbstractTextSerializationService<T, S> {

    public static final String SERVICE_NAME = "json";

    private final ObjectMapper mapper;

    @Override
    protected ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    protected String getExtension() {
        return SERVICE_NAME;
    }

}