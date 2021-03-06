package com.ostapchuk.car.rent.dto;

import java.math.BigDecimal;

public record CarDto(
        Integer id,
        String mark,
        String model,
        BigDecimal rentPricePerHour,
        BigDecimal bookPricePerHour,
        String imgUrl,
        String carStatus
) {
}
