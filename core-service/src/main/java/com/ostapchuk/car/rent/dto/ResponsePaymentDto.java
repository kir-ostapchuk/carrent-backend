package com.ostapchuk.car.rent.dto;

public record ResponsePaymentDto(
        String url,
        boolean success

) {
}
