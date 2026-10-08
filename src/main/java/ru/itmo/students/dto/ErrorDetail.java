package ru.itmo.students.dto;

/**
 * Ошибка в одном поле: какое поле и что с ним не так.
 */
public record ErrorDetail(String field, String message) {
}
