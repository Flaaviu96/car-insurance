package com.example.carins.Exceptions;

import org.springframework.http.HttpStatus;

public class GlobalException extends RuntimeException {
    private final HttpStatus httpStatus;

    protected GlobalException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
