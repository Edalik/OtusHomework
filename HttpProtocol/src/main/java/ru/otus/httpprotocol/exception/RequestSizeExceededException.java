package ru.otus.httpprotocol.exception;

public class RequestSizeExceededException extends RuntimeException {

    public RequestSizeExceededException() {
        super("Request size exceeds the maximum allowed size");
    }

}