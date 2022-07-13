package com.ostapchuk.car.rent.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RideDto(
        LocalDate date,
        String mark,
        String model,
//        String carNumber,
        BigDecimal totalPrice,
        int totalTimeHours,
        List<RideDetailsDto> rideDetailsDtos
) {
}
