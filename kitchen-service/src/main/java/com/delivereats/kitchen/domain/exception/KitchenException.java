package com.delivereats.kitchen.domain.exception;

public class KitchenException extends RuntimeException {

    public KitchenException(String message) {
        super(message);
    }

    public KitchenException(String message, Throwable cause) {
        super(message, cause);
    }
}
