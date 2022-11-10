package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.converter.StatusConverter;
import com.ostapchuk.car.rent.dto.order.OrderDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.CarStatus;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.Role;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.entity.UserStatus;
import com.ostapchuk.car.rent.exception.CarUnavailableException;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.OrderWriteService;
import com.ostapchuk.car.rent.service.PriceService;
import com.ostapchuk.car.rent.service.UserReadService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link FinishingRideStatusProcessor}
 */
@SpringJUnitConfig(classes = {
        FinishingRideStatusProcessor.class, StatusConverter.class,
        UpdatingRideStatusProcessor.class, StartingRideStatusProcessor.class
})
class FinishingRideStatusProcessorTest {

    @Autowired
    private StartingRideStatusProcessor startingRideStatusProcessor;
    @MockBean
    private CarReadService carReadService;
    @MockBean
    private OrderReadService orderReadService;
    @MockBean
    private OrderWriteService orderWriteService;
    @MockBean
    private UserReadService userReadService;
    @MockBean
    private PriceService priceService;

    @BeforeAll
    protected static void beforeAll() {
        defaultOrderDto = new OrderDto(defaultUser.getId(), defaultCar.getId(), CarStatus.IN_BOOKING);
    }

    /**
     * {@link FinishingRideStatusProcessor#process(OrderDto)}
     */
    @Test
    @DisplayName("Car is in rent by the user. The user is active, verified and balance is positive. Should be able to" +
            " finish a ride")
    void process_WhenCarIsInRentAndUserIsVerified_ShouldFinishRide() {
        // given
        final Order order = new Order();

        // when
        when(carReadService.findStartable(defaultCar.getId(), defaultOrderDto.carStatus())).thenReturn(
                Optional.empty());
        when(carReadService.findUpdatable(defaultCar.getId(), defaultOrderDto.carStatus())).thenReturn(
                Optional.empty());
        when(carReadService.findFinishable(defaultCar.getId(), defaultOrderDto.carStatus())).thenReturn(Optional.of(
                defaultCar));
        when(userReadService.findVerifiedById(defaultUser.getId())).thenReturn(defaultUser);
        when(orderReadService.findExistingByUserAndCar(defaultUser, defaultCar)).thenReturn(order);
        when(priceService.calculateRidePrice(order, defaultCar)).thenReturn(new BigDecimal("10"));

        // verify
        startingRideStatusProcessor.process(defaultOrderDto);
        verify(userReadService, times(1)).findVerifiedById(defaultUser.getId());
    }

    /**
     * {@link FinishingRideStatusProcessor#process(OrderDto)}
     */
    @Test
    @DisplayName("The car was already taken by another user or the car status and order status are invalid to process" +
            " the request")
    void process_CarTakenOrCarStatusAndOrderStatusIsInvalid_ShouldNotProcess() {
        // when
        when(carReadService.findStartable(defaultCar.getId(), defaultOrderDto.carStatus())).thenReturn(
                Optional.empty());
        when(carReadService.findUpdatable(defaultCar.getId(), defaultOrderDto.carStatus())).thenReturn(
                Optional.empty());
        when(carReadService.findFinishable(defaultCar.getId(), defaultOrderDto.carStatus())).thenReturn(
                Optional.empty());

        // verify
        final CarUnavailableException thrown = assertThrows(
                CarUnavailableException.class,
                () -> startingRideStatusProcessor.process(defaultOrderDto)
        );
        assertEquals("Sorry, car is not available", thrown.getMessage());
        verify(userReadService, never()).findVerifiedById(defaultUser.getId());
    }

    private static OrderDto defaultOrderDto;
    private static final Car defaultCar = Car.builder()
            .id(1)
            .mark("BMW")
            .model("M5")
            .imgLink("some-img-link")
            .rentPricePerHour(new BigDecimal("10"))
            .bookPricePerHour(new BigDecimal("8"))
            .status(CarStatus.IN_RENT)
            .build();

    private static final User defaultUser = User.builder()
            .id(1L)
            .firstName("FirstName")
            .lastName("LastName")
            .phone("+375332225544")
            .email("user@mailer.com")
            .password("passwordHash1231234")
            .role(Role.USER)
            .status(UserStatus.ACTIVE)
            .verified(true)
            .balance(BigDecimal.ZERO)
            .passportImgUrl("someurl")
            .drivingLicenseImgUrl("someurl")
            .build();

}
