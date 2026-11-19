package com.ecom.auth.exception;

public class InvalidSignupException extends RuntimeException {

    public InvalidSignupException() {
        super("Password must be at most 72 UTF-8 bytes");
    }
}
