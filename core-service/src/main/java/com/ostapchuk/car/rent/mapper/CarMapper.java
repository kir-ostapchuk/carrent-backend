package com.ostapchuk.car.rent.mapper;

import com.ostapchuk.car.rent.dto.CarDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.CarStatus;
import org.springframework.stereotype.Component;

@Component
public record CarMapper() {

    public Car toEntity(final CarDto carDto) {
        return Car.builder()
                .id(carDto.id())
                .mark(carDto.mark())
                .model(carDto.model())
                .rentPricePerHour(carDto.rentPricePerHour())
                .bookPricePerHour(carDto.bookPricePerHour())
                .status(CarStatus.valueOf(carDto.carStatus()))
                .build();
    }

    public CarDto toDto(final Car car) {
        return new CarDto(
                car.getId(),
                car.getMark(),
                car.getModel(),
                car.getRentPricePerHour(),
                car.getBookPricePerHour(),
                car.getImgLink(),
                car.getStatus().toString()
        );
    }
}
