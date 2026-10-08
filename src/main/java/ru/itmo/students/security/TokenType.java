package ru.itmo.students.security;

/**
 * Тип JWT. Записывается в токен, чтобы refresh-токен нельзя было подставить вместо access и наоборот.
 */
public enum TokenType {
    ACCESS,
    REFRESH
}
