package com.database.services.exceptions;

public class InvalidParameterProvidedException extends RuntimeException {
    public InvalidParameterProvidedException(String errorMessage) {
        super(errorMessage);
    }
}
