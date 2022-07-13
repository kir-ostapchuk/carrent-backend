package com.ostapchuk.car.rent.exception;

public class BadStatusException extends RuntimeException {

    public BadStatusException(final String message) {
        super(message);
    }
}
