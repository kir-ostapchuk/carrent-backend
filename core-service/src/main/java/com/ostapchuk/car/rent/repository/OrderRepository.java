package com.ostapchuk.car.rent.repository;

import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.OrderStatus;
import com.ostapchuk.car.rent.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findFirstByUserAndEndingIsNull(User user);

    List<Order> findAllByUuid(String uuid);

    Optional<Order> findFirstByUserAndCarAndEndingIsNullAndStatusOrderByStartDesc(User user, Car car,
                                                                                  OrderStatus status);

    List<Order> findAllByUserAndEndingIsNotNull(User user);
}
