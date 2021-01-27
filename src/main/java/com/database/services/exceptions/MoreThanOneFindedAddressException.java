package com.database.services.exceptions;

public class MoreThanOneFindedAddressException extends RuntimeException {
    public MoreThanOneFindedAddressException(String errorMessage) {
        super(errorMessage);
    }
}