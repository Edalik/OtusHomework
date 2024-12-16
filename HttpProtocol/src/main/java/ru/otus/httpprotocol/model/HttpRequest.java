package ru.otus.httpprotocol.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class HttpRequest {

    private String method;

    private String uri;

    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> queryParams = new HashMap<>();

    private String body;

}