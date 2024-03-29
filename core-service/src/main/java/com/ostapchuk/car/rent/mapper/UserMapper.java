package com.ostapchuk.car.rent.mapper;

import com.ostapchuk.car.rent.dto.user.RegisterUserDto;
import com.ostapchuk.car.rent.dto.user.UserDto;
import com.ostapchuk.car.rent.entity.User;
import org.springframework.stereotype.Component;

@Component
public record UserMapper() {

    public UserDto toDto(final User user) {
        return new UserDto(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getEmail(),
                null,
                user.getStatus().toString(),
                user.isVerified(),
                user.getRole().toString(),
                user.getBalance(),
                user.getPassportImgUrl(),
                user.getDrivingLicenseImgUrl()
        );
    }

    public User toEntity(final RegisterUserDto userDto) {
        return User.builder()
                .firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .email(userDto.email())
                .phone(userDto.phone())
                .build();
    }
}
