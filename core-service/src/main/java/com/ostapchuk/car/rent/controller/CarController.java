package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.CarDto;
import com.ostapchuk.car.rent.dto.CarsDto;
import com.ostapchuk.car.rent.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping
    @PreAuthorize("hasAuthority('users:write')")
    public void save(@RequestBody final CarDto carDto) {
        carService.save(carDto);
    }

    @GetMapping
    public CarsDto findAll() {
        return carService.findAll();
    }

    @GetMapping("/free")
    public CarsDto findAllFree() {
        return carService.findAllFree();
    }

    @GetMapping("/available/{userId}")
    public CarsDto findAllAvailable(@PathVariable final Long userId) {
        return carService.findAllAvailableForUser(userId);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('users:read')")
    public CarDto findById(@PathVariable final Integer id) {
        return carService.findDtoById(id);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('users:write')")
    public void update(@RequestBody final CarDto carDto) {
        carService.update(carDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('users:delete')")
    public void delete(@PathVariable final Integer id) {
        carService.delete(id);
    }
}
