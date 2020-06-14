package com.warehouse.Exception;


public class AuthRequiredException extends Exception {
    public AuthRequiredException() {
    }

    public AuthRequiredException(String message) {
        super(message);
    }
}
