package com.ostapchuk.car.rent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

import static javax.persistence.EnumType.STRING;

// TODO: 3/17/2022 add documentation for car (license)
// TODO: 3/17/2022 car number

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "mark", nullable = false, length = 30)
    private String mark;

    @Column(name = "model", nullable = false, length = 30)
    private String model;

    @Column(name = "img_link")
    private String imgLink;

    @Column(name = "rent_price_per_hour", nullable = false, precision = 5, scale = 2)
    private BigDecimal rentPricePerHour;

    @Column(name = "book_price_per_hour", nullable = false, precision = 5, scale = 2)
    private BigDecimal bookPricePerHour;

    @Enumerated(STRING)
    @Column(name = "status", nullable = false)
    private CarStatus status;
}