package com.ostapchuk.car.rent.dto.ride;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RideDetailsDto(
        LocalDateTime start,
        LocalDateTime end,
        String status,
        BigDecimal price
) {
}
