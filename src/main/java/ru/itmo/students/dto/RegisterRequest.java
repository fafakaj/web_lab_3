package ru.itmo.students.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotNull(message = "Обязательное поле")
        @Pattern(regexp = "[a-z0-9_]{3,32}",
                message = "Логин — от 3 до 32 символов: латинские буквы, цифры и _")
        String username,

        @NotNull(message = "Обязательное поле")
        @Size(max = 254, message = "Почта — не длиннее 254 символов")
        @Email(regexp = ".+@.+\\..+", message = "Почта — адрес вида name@example.com")
        String email,

        @NotNull(message = "Обязательное поле")
        @Size(min = 8, max = 64, message = "Пароль — от 8 до 64 символов")
        @Pattern(regexp = "(?=.*[A-Za-zА-ЯЁа-яё])(?=.*[0-9]).*",
                message = "Пароль должен содержать хотя бы одну букву и одну цифру")
        String password
) {
    public RegisterRequest {
        username = username == null ? null : username.strip().toLowerCase();
        email = email == null ? null : email.strip().toLowerCase();
    }
}
