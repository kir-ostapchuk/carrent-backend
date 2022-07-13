package com.ostapchuk.car.rent.exception;

public class JwtAuthenticationException extends RuntimeException {

    public JwtAuthenticationException(final String msg) {
        super(msg);
    }
}
