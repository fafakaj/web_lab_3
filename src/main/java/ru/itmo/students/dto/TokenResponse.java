package ru.itmo.students.dto;

import jakarta.json.bind.annotation.JsonbProperty;

/**
 * Ответ на вход и обновление токена.
 * access_token — для заголовка Authorization: Bearer ..., живёт expires_in секунд.
 * refresh_token — чтобы получить новую пару токенов без логина и пароля.
 */
public record TokenResponse(
        @JsonbProperty("access_token") String accessToken,
        @JsonbProperty("refresh_token") String refreshToken,
        @JsonbProperty("token_type") String tokenType,
        @JsonbProperty("expires_in") long expiresIn,
        UserResponse user
) {
}
