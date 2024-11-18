package com.gr2.CVNest.util.error;

public class NameAlreadyExistException extends RuntimeException {
    public NameAlreadyExistException(String message) {
        super(message);
    }
}
