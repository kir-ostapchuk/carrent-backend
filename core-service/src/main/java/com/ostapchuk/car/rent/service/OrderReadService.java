package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.converter.StatusConverter;
import com.ostapchuk.car.rent.dto.RideDetailsDto;
import com.ostapchuk.car.rent.dto.RideDto;
import com.ostapchuk.car.rent.dto.RidesDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.OrderCreationException;
import com.ostapchuk.car.rent.repository.OrderRepository;
import com.ostapchuk.car.rent.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ostapchuk.car.rent.entity.OrderStatus.RENT;
import static com.ostapchuk.car.rent.entity.OrderStatus.RENT_PAUSED;

@Service
@RequiredArgsConstructor
public class OrderReadService {

    private final OrderRepository orderRepository;
    private final UserReadService userReadService;
    private final StatusConverter statusConverter;

    // query
    public RidesDto findAllRidesByUserId(final Long id) {
        final User user = userReadService.findById(id);
        final Map<String, List<Order>> rides = orderRepository.findAllByUserAndEndingIsNotNull(user).stream()
                .collect(Collectors.groupingBy(Order::getUuid));
        final List<RideDto> ridesDto = processRides(rides);
        return new RidesDto(ridesDto);
    }

    Order findExistingOrder(final User user, final Car car, final String message) {
        return orderRepository.findFirstByUserAndCarAndEndingIsNullAndStatusOrderByStartDesc(user, car,
                        statusConverter.toOrderStatus(car.getStatus()))
                .orElseThrow(() -> new OrderCreationException(message));
    }


    BigDecimal calculatePrice(final Order order, final Car car) {
        final long hours = DateTimeUtil.retrieveDurationInHours(order.getStart(), order.getEnding());
        final BigDecimal hourPrice = switch (order.getStatus()) {
            case BOOKING -> car.getBookPricePerHour();
            case RENT, RENT_PAUSED -> car.getRentPricePerHour();
        };
        return hourPrice.multiply(new BigDecimal(hours));
    }

    BigDecimal calculateRidePrice(final Order order, final Car car) {
        final BigDecimal price = calculatePrice(order, car);
        if (RENT.equals(order.getStatus()) || RENT_PAUSED.equals(order.getStatus())) {
            final List<Order> orders = orderRepository.findAllByUuid(order.getUuid());
            return retrieveRidePriceByOrders(orders).add(price);
        } else {
            return price;
        }
    }

    private List<RideDto> processRides(final Map<String, List<Order>> rides) {
        final List<RideDto> ridesDto = new ArrayList<>();
        for (final Map.Entry<String, List<Order>> entry : rides.entrySet()) {
            final List<RideDetailsDto> rideDetailsDtos = new ArrayList<>();
            final List<Order> orders = entry.getValue().stream()
                    .sorted(Comparator.comparing(Order::getStart))
                    .toList();
            orders.forEach(order -> rideDetailsDtos.add(new RideDetailsDto(order.getStart(), order.getEnding(),
                    order.getStatus().toString(), order.getPrice())));
            final Order order = orders.get(0);
            final Car car = order.getCar();
            ridesDto.add(new RideDto(order.getStart().toLocalDate(), car.getMark(), car.getModel(),
                    retrieveRidePriceByOrders(orders), retrieveRideTimeByOrders(orders), rideDetailsDtos));
        }
        return ridesDto.stream()
                .sorted(Comparator.comparing(RideDto::date).reversed())
                .toList();
    }

    // TODO: 3/17/2022 try to use reduce
    private int retrieveRideTimeByOrders(final List<Order> orders) {
        final int[] totalHours = {0};
        orders.forEach(o -> totalHours[0] += DateTimeUtil.retrieveDurationInHours(o.getStart(), o.getEnding()));
        return totalHours[0];
    }

    private BigDecimal retrieveRidePriceByOrders(final List<Order> orders) {
        return orders.stream()
                .map(Order::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}