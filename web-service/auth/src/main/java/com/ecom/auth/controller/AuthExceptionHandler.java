package com.ecom.auth.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecom.auth.exception.InvalidCredentialsException;
import com.ecom.auth.exception.InvalidSignupException;
import com.ecom.auth.exception.UsernameAlreadyExistsException;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> invalidCredentials() {
        return error(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> duplicateUsername() {
        return error(HttpStatus.CONFLICT, "Username is already registered");
    }

    @ExceptionHandler(InvalidSignupException.class)
    public ResponseEntity<Map<String, String>> invalidSignup() {
        return error(HttpStatus.BAD_REQUEST, "Password must be at most 72 UTF-8 bytes");
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class, HttpMessageNotReadableException.class })
    public ResponseEntity<Map<String, String>> invalidRequest() {
        return error(HttpStatus.BAD_REQUEST, "Request validation failed");
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}
