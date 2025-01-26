package ru.otus.serialization.service.text_serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service(YmlSerializationService.SERVICE_NAME)
public class YmlSerializationService<T, S> extends AbstractTextSerializationService<T, S> {

    public static final String SERVICE_NAME = "yml";

    private final YAMLMapper mapper;

    @Override
    protected ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    protected String getExtension() {
        return SERVICE_NAME;
    }

}