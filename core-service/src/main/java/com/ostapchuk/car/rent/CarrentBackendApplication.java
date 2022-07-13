package com.ostapchuk.car.rent;

import com.ostapchuk.car.rent.config.FrontendLinkConfig;
import com.ostapchuk.car.rent.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableConfigurationProperties({JwtConfig.class, FrontendLinkConfig.class})
public class CarrentBackendApplication {

    public static void main(final String[] args) {
        SpringApplication.run(CarrentBackendApplication.class, args);
    }

}
