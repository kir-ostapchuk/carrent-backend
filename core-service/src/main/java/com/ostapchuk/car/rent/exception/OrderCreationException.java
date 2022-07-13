package com.ostapchuk.car.rent.exception;

public class OrderCreationException extends RuntimeException {

    public OrderCreationException(final String message) {
        super(message);
    }
}
