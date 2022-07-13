package com.ostapchuk.car.rent.exception;

import com.ostapchuk.car.rent.dto.ErrorResponseDto;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
public record ExceptionAdviser() {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({EntityNotFoundException.class, OrderCreationException.class, BalanceException.class,
            UserUnverifiedException.class})
    public ErrorResponseDto entityNotFoundException(final RuntimeException ex) {
        return new ErrorResponseDto(ex.getMessage(), BAD_REQUEST.value());
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponseDto accessDeniedException(final AccessDeniedException ex) {
        return new ErrorResponseDto(ex.getMessage(), FORBIDDEN.value());
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(JwtAuthenticationException.class)
    public ErrorResponseDto jwtAuthenticationException(final JwtAuthenticationException ex) {
        return new ErrorResponseDto(ex.getMessage(), UNAUTHORIZED.value());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public ErrorResponseDto generalException(final Throwable t) {
        return new ErrorResponseDto("Some error occurred: " + t.getMessage(), INTERNAL_SERVER_ERROR.value());
    }
}
