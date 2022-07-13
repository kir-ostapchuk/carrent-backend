package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.converter.StatusConverter;
import com.ostapchuk.car.rent.dto.OrderDto;
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

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.ostapchuk.car.rent.entity.CarStatus.FREE;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_BOOKING;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_RENT;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_RENT_PAUSED;
import static com.ostapchuk.car.rent.entity.OrderStatus.RENT;
import static com.ostapchuk.car.rent.entity.OrderStatus.RENT_PAUSED;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CarService carService;
    private final UserService userService;
    private final StatusConverter statusConverter;

    private final Random random = new Random(); // TODO: 3/17/2022 plusHours(random.nextInt(25) + 1L)

    @Transactional
    public void process(final OrderDto orderDto) {
        final User user = userService.findById(orderDto.userId());
        userService.verifyUser(user);
        final Car car = carService.findById(orderDto.carId());
        if (isStartRide(orderDto, car)) {
            startRide(orderDto, user, car);
        } else if (isUpdateRide(orderDto, car)) {
            updateStatus(orderDto, user, car);
        } else if (isFinishRide(orderDto, car)) {
            finishRide(orderDto, user, car);
        } else {
            throw new OrderCreationException("Could not create order");
        }
    }

    public RidesDto findAllRidesByUserId(final Long id) {
        final User user = userService.findById(id);
        final Map<String, List<Order>> rides = orderRepository.findAllByUserAndEndingIsNotNull(user).stream()
                .collect(Collectors.groupingBy(Order::getUuid));
        final List<RideDto> ridesDto = processRides(rides);
        return new RidesDto(ridesDto);
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

    private boolean isStartRide(final OrderDto orderDto, final Car car) {
        return FREE.equals(car.getStatus()) &&
                (IN_BOOKING.equals(orderDto.carStatus()) || IN_RENT.equals(orderDto.carStatus()));
    }

    private boolean isFinishRide(final OrderDto orderDto, final Car car) {
        return (IN_RENT_PAUSED.equals(car.getStatus()) || IN_RENT.equals(car.getStatus()) ||
                IN_BOOKING.equals(car.getStatus())) && FREE.equals(orderDto.carStatus());
    }

    private boolean isUpdateRide(final OrderDto orderDto, final Car car) {
        return (IN_BOOKING.equals(car.getStatus()) && IN_RENT.equals(orderDto.carStatus())) ||
                (IN_RENT.equals(car.getStatus()) && IN_RENT_PAUSED.equals(orderDto.carStatus())) ||
                (IN_RENT_PAUSED.equals(car.getStatus()) && IN_RENT.equals(orderDto.carStatus()));
    }

    private BigDecimal calculateRidePrice(final Order order, final Car car) {
        final BigDecimal price = calculatePrice(order, car);
        if (RENT.equals(order.getStatus()) || RENT_PAUSED.equals(order.getStatus())) {
            final List<Order> orders = orderRepository.findAllByUuid(order.getUuid());
            return retrieveRidePriceByOrders(orders).add(price);
        } else {
            return price;
        }
    }

    private BigDecimal retrieveRidePriceByOrders(final List<Order> orders) {
        return orders.stream()
                .map(Order::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // TODO: 3/17/2022 try to use reduce
    private int retrieveRideTimeByOrders(final List<Order> orders) {
        final int[] totalHours = {0};
        orders.forEach(o -> totalHours[0] += DateTimeUtil.retrieveDurationInHours(o.getStart(), o.getEnding()));
        return totalHours[0];
    }

    private void updateStatus(final OrderDto orderDto, final User user, final Car car) {
        final Order order = findExistingOrder(user, car, "Could not update order status");
        order.setEnding(LocalDateTime.now().plusHours(random.nextInt(25) + 1L));
        order.setPrice(calculatePrice(order, car));
        orderRepository.save(order);
        car.setStatus(orderDto.carStatus());
        createOrderUsingExistingOrder(orderDto, user, car, order);
    }

    private void createOrderUsingExistingOrder(final OrderDto orderDto, final User user, final Car car,
                                               final Order order) {
        final Order newOrder = Order.builder()
                .user(user)
                .uuid(order.getUuid())
                .start(LocalDateTime.now())
                .car(car)
                .status(statusConverter.toOrderStatus(orderDto.carStatus()))
                .build();
        orderRepository.save(newOrder);
    }

    private void finishRide(final OrderDto orderDto, final User user, final Car car) {
        final Order order = findExistingOrder(user, car, "Could not finish the order");
        order.setEnding(LocalDateTime.now().plusHours(random.nextInt(25) + 1L));
        order.setPrice(calculatePrice(order, car));
        user.setBalance(user.getBalance().subtract(calculateRidePrice(order, car)));
        car.setStatus(orderDto.carStatus());
        orderRepository.save(order);
    }

    private Order findExistingOrder(final User user, final Car car, final String message) {
        return orderRepository.findFirstByUserAndCarAndEndingIsNullAndStatusOrderByStartDesc(user, car,
                        statusConverter.toOrderStatus(car.getStatus()))
                .orElseThrow(() -> new OrderCreationException(message));
    }

    private void startRide(final OrderDto orderDto, final User user, final Car car) {
        final AtomicBoolean theSameCar = new AtomicBoolean(true);
        orderRepository.findFirstByUserAndEndingIsNull(user)
                .map(Order::getCar)
                .map(Car::getId)
                .ifPresent(id -> theSameCar.set(id.equals(car.getId())));
        if (!theSameCar.get()) {
            throw new OrderCreationException("Could not create another order");
        }
        car.setStatus(orderDto.carStatus());
        createOrder(orderDto, user, car);
    }

    private void createOrder(final OrderDto orderDto, final User user, final Car car) {
        final Order order = Order.builder()
                .user(user)
                .uuid(UUID.randomUUID().toString())
                .start(LocalDateTime.now())
                .car(car)
                .status(statusConverter.toOrderStatus(orderDto.carStatus()))
                .build();
        orderRepository.save(order);
    }

    private BigDecimal calculatePrice(final Order order, final Car car) {
        final long hours = DateTimeUtil.retrieveDurationInHours(order.getStart(), order.getEnding());
        final BigDecimal hourPrice = switch (order.getStatus()) {
            case BOOKING -> car.getBookPricePerHour();
            case RENT, RENT_PAUSED -> car.getRentPricePerHour();
        };
        return hourPrice.multiply(new BigDecimal(hours));
    }
}
