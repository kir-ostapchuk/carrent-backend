package com.ostapchuk.car.rent.entity;

public enum Permission {

    USERS_READ("users:read"),
    USERS_WRITE("users:write"),
    USERS_DELETE("users:delete");

    private final String permission;

    Permission(final String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}