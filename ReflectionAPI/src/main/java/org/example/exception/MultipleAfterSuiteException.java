package org.example.exception;

public class MultipleAfterSuiteException extends RuntimeException {

    public MultipleAfterSuiteException(String className) {
        super("Multiple @AfterSuite annotations applied in class: " + className);
    }

}