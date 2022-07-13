package com.ostapchuk.car.rent.dto;

import com.ostapchuk.car.rent.entity.UserStatus;

import java.util.Set;

public record StatusesDto(Set<UserStatus> statuses) {
}
