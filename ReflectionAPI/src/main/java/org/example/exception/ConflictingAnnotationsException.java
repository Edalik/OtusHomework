package org.example.exception;

public class ConflictingAnnotationsException extends RuntimeException {

    public ConflictingAnnotationsException(String method) {
        super("Conflicting annotations on method: " + method);
    }

}