package ru.otus.homework.exception;

public class ConflictingAnnotationsException extends RuntimeException {

    public ConflictingAnnotationsException(String method) {
        super("Conflicting annotations on method: " + method);
    }

}