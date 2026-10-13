package ru.itmo.students.dto;

import jakarta.json.bind.annotation.JsonbProperty;

public record TokenResponse(
        @JsonbProperty("access_token") String accessToken,
        @JsonbProperty("refresh_token") String refreshToken,
        @JsonbProperty("token_type") String tokenType,
        @JsonbProperty("expires_in") long expiresIn,
        UserResponse user
) {
}
