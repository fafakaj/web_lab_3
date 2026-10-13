package ru.itmo.students.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @JsonbProperty("refresh_token")
        @NotBlank(message = "Обязательное поле")
        String refreshToken
) {
}
