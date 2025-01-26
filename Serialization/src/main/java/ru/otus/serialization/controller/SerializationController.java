package ru.otus.serialization.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.serialization.enums.SerializationType;
import ru.otus.serialization.service.interfaces.SerializationService;

import java.io.IOException;

@RestController
@RequestMapping("/serialization")
@RequiredArgsConstructor
public class SerializationController {

    private final SerializationService serializationService;

    @RequestMapping
    public ResponseEntity<Object> serialization(@RequestHeader(HttpHeaders.ACCEPT) MediaType mediaType) throws IOException {
        SerializationType serializationType;
        switch (mediaType.toString()) {
            case MediaType.APPLICATION_JSON_VALUE -> serializationType = SerializationType.JSON;
            case MediaType.APPLICATION_YAML_VALUE -> serializationType = SerializationType.YML;
            case "application/java" -> serializationType = SerializationType.JAVA;
            default -> {
                return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            }
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        if (serializationType == SerializationType.JAVA) {
            mediaType = MediaType.APPLICATION_JSON;
        }
        responseHeaders.setContentType(mediaType);
        return new ResponseEntity<>(serializationService.serialization(serializationType), responseHeaders, HttpStatus.OK);
    }

}