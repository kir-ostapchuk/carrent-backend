package com.ostapchuk.car.rent.dto;

import java.math.BigDecimal;

public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String phone,
        String email,
        String password,
        String status,
        Boolean verified,
        String role,
        BigDecimal balance,
        String documentImg1,
        String documentImg2
) {
}
