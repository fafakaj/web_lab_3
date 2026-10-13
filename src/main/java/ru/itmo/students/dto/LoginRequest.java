package ru.itmo.students.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Обязательное поле")
        String username,

        @NotBlank(message = "Обязательное поле")
        String password
) {
    public LoginRequest {
        username = username == null ? null : username.strip().toLowerCase();
    }
}
