package com.ostapchuk.car.rent.dto;

public record RequestPaymentDto(
        Long userId,
        String successUrl,
        String cancelUrl
) {
}
