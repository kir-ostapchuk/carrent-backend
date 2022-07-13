package com.ostapchuk.car.rent.exception;

public class UserUnverifiedException extends RuntimeException {

    public UserUnverifiedException(final String message) {
        super(message);
    }
}
