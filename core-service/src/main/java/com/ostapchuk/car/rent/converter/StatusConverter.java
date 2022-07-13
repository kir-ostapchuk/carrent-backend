package com.ostapchuk.car.rent.converter;

import com.ostapchuk.car.rent.entity.CarStatus;
import com.ostapchuk.car.rent.entity.OrderStatus;
import com.ostapchuk.car.rent.exception.BadStatusException;
import org.springframework.stereotype.Component;

import static com.ostapchuk.car.rent.entity.OrderStatus.BOOKING;
import static com.ostapchuk.car.rent.entity.OrderStatus.RENT;
import static com.ostapchuk.car.rent.entity.OrderStatus.RENT_PAUSED;

@Component
public class StatusConverter {

    public OrderStatus toOrderStatus(final CarStatus carStatus) {
        return switch (carStatus) {
            case IN_BOOKING -> BOOKING;
            case IN_RENT -> RENT;
            case IN_RENT_PAUSED -> RENT_PAUSED;
            default -> throw new BadStatusException("Could not convert car status " + carStatus);
        };
    }
}
