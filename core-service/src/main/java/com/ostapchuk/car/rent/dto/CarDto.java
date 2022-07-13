package com.ostapchuk.car.rent.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record CarDto(
        Integer id,
        String mark,
        String model,
        BigDecimal rentPricePerHour,
        BigDecimal bookPricePerHour,
        String imgUrl,
        String carStatus
) implements Serializable {
    private static final long serialVersionUID = 1234526077883281623L;
}
