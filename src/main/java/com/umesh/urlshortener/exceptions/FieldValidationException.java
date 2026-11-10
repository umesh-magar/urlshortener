package com.umesh.urlshortener.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class FieldValidationException extends ApiException {
        public FieldValidationException(String field, String message) {
            super(HttpStatus.BAD_REQUEST, message, Map.of(field, message));
        }
    }

