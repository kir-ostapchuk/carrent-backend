package com.ostapchuk.car.rent.dto;

import com.ostapchuk.car.rent.entity.Role;

import java.util.Set;

public record RolesDto(Set<Role> roles) {
}
