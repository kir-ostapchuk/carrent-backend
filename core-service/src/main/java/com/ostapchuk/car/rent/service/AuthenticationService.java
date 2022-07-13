package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.AuthenticationRequestDto;
import com.ostapchuk.car.rent.dto.AuthenticationResponseDto;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticationResponseDto login(final AuthenticationRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        final User user = userService.findByEmail(request.getEmail());
        final String token = jwtTokenProvider.createToken(request.getEmail(), user.getRole().name());
        return new AuthenticationResponseDto(user.getId(), token, user.getRole().name());
    }
}
