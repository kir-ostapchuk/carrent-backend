package com.ostapchuk.car.rent.dto;

import com.ostapchuk.car.rent.entity.CarStatus;

public record OrderDto(
        Long userId,
        Integer carId,
        CarStatus carStatus
) {
}
