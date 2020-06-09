package com.warehouse.exceptions;

public class AuthWrongException extends Exception {
    public AuthWrongException() {
    }

    public AuthWrongException(String message) {
        super(message);
    }
}
