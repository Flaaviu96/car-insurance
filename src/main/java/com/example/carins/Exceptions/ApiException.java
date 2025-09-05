package com.example.carins.Exceptions;

import org.springframework.http.HttpStatus;

public class ApiException extends GlobalException {
    public ApiException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
