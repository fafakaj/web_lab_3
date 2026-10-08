package ru.itmo.students.dto;

/**
 * Пользователь в ответе API. Хеша пароля здесь нет: он никогда не уходит клиенту.
 */
public record UserResponse(Long id, String username, String email, String role) {
}
