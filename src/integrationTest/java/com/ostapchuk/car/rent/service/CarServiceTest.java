package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.CarDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.datasource.url=jdbc:tc:postgresql:14///")
@ActiveProfiles("test")
class CarServiceTest {

    @Autowired
    WebTestClient webTestClient;

    private CarDto carDto;

//    @BeforeEach
//    void setUp() {
//        carDto = new CarDto(
//                null,
//                "Mazda",
//                "6",
//                BigDecimal.valueOf(6.6),
//                BigDecimal.valueOf(3.2),
//                "https://cdn.atlantm.com/static/multifile/326/2134/mazda6_active_Blue_Reflex_1n.png",
//                CarStatus.UNAVAILABLE.toString()
//        );
//    }
//
//    @Test
//    void save() {
//        webTestClient
//                .post()
//                .uri("/api/v1/cars")
//                .bodyValue(carDto)
//                .exchange()
//                .expectStatus().is2xxSuccessful()
//                .expectBody(CarDto.class);
//    }
}