package com.ostapchuk.car.rent.dto;

import lombok.Data;

@Data
public class AuthenticationRequestDto {

    private String email;
    private String password;

}
