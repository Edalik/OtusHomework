package ru.otus.httpprotocol.exception;

public class BusinessException extends RuntimeException {

    public BusinessException(Throwable cause) {
        super(cause);
    }

}