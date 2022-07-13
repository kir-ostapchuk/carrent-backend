package com.ostapchuk.car.rent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "frontend")
public record FrontendLinkConfig(
        String link
) {
}
