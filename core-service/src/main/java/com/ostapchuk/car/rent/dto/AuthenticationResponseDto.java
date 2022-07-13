package com.ostapchuk.car.rent.dto;

public record AuthenticationResponseDto(
        Long userId,
        String token,
        String role
) {
}
