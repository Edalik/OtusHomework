package ru.otus.dbinteracion.exception;

public class ConnectionException extends RuntimeException {

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

}