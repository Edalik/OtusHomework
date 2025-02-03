package ru.otus.serialization.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SerializationType {
    JSON("json"),
    YML("yml"),
    JAVA("java");

    private final String extension;
}