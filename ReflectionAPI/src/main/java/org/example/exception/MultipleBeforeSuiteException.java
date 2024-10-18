package org.example.exception;

public class MultipleBeforeSuiteException extends RuntimeException {

    public MultipleBeforeSuiteException(String className) {
        super("Multiple @BeforeSuite annotations applied in class: " + className);
    }

}