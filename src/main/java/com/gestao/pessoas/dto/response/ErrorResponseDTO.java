package com.gestao.pessoas.dto.response;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String timestamp,
        int status,
        String error,
        String message
) {
    public static ErrorResponseDTO of(HttpStatus status, String message) {
        return new ErrorResponseDTO(
                LocalDateTime.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                message
        );
    }
}
