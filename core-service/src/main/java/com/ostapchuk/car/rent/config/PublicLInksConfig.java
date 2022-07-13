package com.ostapchuk.car.rent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class PublicLInksConfig {

    @Bean
    public List<String> publicLinks() {
        return Collections.emptyList();
    }
}
