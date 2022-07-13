package com.ostapchuk.car.rent.mapper;

import com.ostapchuk.car.rent.dto.UserDto;
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
                user.getDocument() == null ? null : user.getDocument().getImgLink1(),
                user.getDocument() == null ? null : user.getDocument().getImgLink2()
        );
    }
}
