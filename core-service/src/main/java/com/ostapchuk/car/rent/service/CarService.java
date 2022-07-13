package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.CarDto;
import com.ostapchuk.car.rent.dto.CarsDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.CarStatus;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.mapper.CarMapper;
import com.ostapchuk.car.rent.repository.CarRepository;
import com.ostapchuk.car.rent.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CarMapper carMapper;

    public void save(final CarDto carDto) {
        carRepository.save(carMapper.toEntity(carDto));
    }

    public void update(final CarDto carDto) {
        final Integer id = carDto.id();
        final Car car = findById(id);
        car.setMark(carDto.mark());
        car.setModel(carDto.model());
        car.setBookPricePerHour(carDto.bookPricePerHour());
        car.setRentPricePerHour(carDto.rentPricePerHour());
        car.setImgLink(carDto.imgUrl());
        carRepository.save(car);
    }

    public void delete(final Integer id) {
        carRepository.delete(findById(id));
    }

    public CarsDto findAll() {
        final List<CarDto> carsDto = carRepository.findAllByOrderById().stream()
                .map(carMapper::toDto)
                .toList();
        return new CarsDto(carsDto);
    }

    public CarDto findDtoById(final Integer id) {
        return carMapper.toDto(findById(id));
    }

    public Car findById(final Integer id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find car with id: " + id));
    }

    public CarsDto findAllFree() {
        final List<CarDto> carsDto = carRepository.findAllByStatusOrderById(CarStatus.FREE).stream()
                .map(carMapper::toDto)
                .toList();
        return new CarsDto(carsDto);
    }

//    todo: Fix cache when a user changes car's status
//     @Cacheable(value = "cars")
    public CarsDto findAllAvailableForUser(final Long userId) {
        final List<Car> freeCars = new ArrayList<>();
        orderRepository.findFirstByUserAndEndingIsNull(userService.findById(userId))
                .map(Order::getCar)
                .ifPresent(freeCars::add);
        freeCars.addAll(carRepository.findAllByStatusOrderById(CarStatus.FREE));
        final List<CarDto> carsDto = freeCars.stream()
                .map(carMapper::toDto)
                .toList();
        return new CarsDto(carsDto);
    }
}
