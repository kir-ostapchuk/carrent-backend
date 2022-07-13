package com.ostapchuk.car.rent.dto;

import java.io.Serializable;
import java.util.List;

public record CarsDto(List<CarDto> carsDto) implements Serializable {
    private static final long serialVersionUID = 7156526077883281623L;
}
