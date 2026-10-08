package ru.itmo.students.dto;

import java.util.List;

/**
 * Одна страница списка: записи страницы, номер страницы (с 1), размер страницы и сколько записей всего.
 */
public record PageResponse<T>(List<T> items, int page, int size, long total) {
}
