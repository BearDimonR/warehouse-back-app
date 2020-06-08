package com.warehouse.exceptions;


public class AuthRequiredException extends Exception {
    public AuthRequiredException() {
    }

    public AuthRequiredException(String message) {
        super(message);
    }
}
