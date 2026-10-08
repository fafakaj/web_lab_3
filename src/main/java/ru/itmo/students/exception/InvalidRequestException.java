package ru.itmo.students.exception;

import java.util.List;

/**
 * 400: запрос нельзя разобрать (нет тела, пустой PATCH и т. п.).
 */
public class InvalidRequestException extends ApiException {

    public InvalidRequestException(String message) {
        super(400, "BAD_REQUEST", message, List.of());
    }
}
