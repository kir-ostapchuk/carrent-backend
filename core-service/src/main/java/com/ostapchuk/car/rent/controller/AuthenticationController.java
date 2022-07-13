package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.AuthenticationRequestDto;
import com.ostapchuk.car.rent.dto.AuthenticationResponseDto;
import com.ostapchuk.car.rent.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public AuthenticationResponseDto authenticate(@RequestBody final AuthenticationRequestDto request) {
        return authenticationService.login(request);
    }

    @PostMapping("/logout")
    public void logout(final HttpServletRequest request, final HttpServletResponse response) {
        final SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
    }
}
