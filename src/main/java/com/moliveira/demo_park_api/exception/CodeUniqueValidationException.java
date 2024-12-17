package com.moliveira.demo_park_api.exception;

public class CodeUniqueValidationException extends RuntimeException {

    public CodeUniqueValidationException(String message) {
        super(message);
    }
}
