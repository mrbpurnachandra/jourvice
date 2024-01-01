package com.mrbpurnachandra.jourvicebackend.exceptions;

public abstract class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
