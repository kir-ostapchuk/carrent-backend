package com.ostapchuk.car.rent.dto;

public record UploadRequestDto(
        Long userId,
        int imgNumber
) {
}
