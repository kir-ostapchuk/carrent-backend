package com.ostapchuk.car.rent.dto;

public record RegisterUserDto(
        String firstName,
        String lastName,
        String phone,
        String email,
        String password
) {
}
