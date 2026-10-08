package ru.itmo.students.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * Тело POST /api/auth/refresh и POST /api/auth/logout.
 */
public record RefreshRequest(
        @JsonbProperty("refresh_token")
        @NotBlank(message = "Обязательное поле")
        String refreshToken
) {
}
