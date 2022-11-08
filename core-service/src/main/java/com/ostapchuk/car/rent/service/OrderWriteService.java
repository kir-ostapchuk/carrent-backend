package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.order.OrderDto;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.processor.StartingRideStatusProcessor;
import com.ostapchuk.car.rent.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderWriteService {

    private final StartingRideStatusProcessor startingRideStatusProcessor;
    private final OrderRepository orderRepository;

    public void process(final OrderDto orderDto) {
        startingRideStatusProcessor.process(orderDto);
    }

    public Order save(final Order order) {
        return orderRepository.save(order);
    }
}
