package com.ostapchuk.car.rent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "jwt")
public record JwtConfig(
        String secret,
        String header,
        @DurationUnit(ChronoUnit.MILLIS) Duration expiration,
        RefreshJwt refresh
) {
    public record RefreshJwt(@DurationUnit(ChronoUnit.MILLIS) Duration expiration) {
    }
}
