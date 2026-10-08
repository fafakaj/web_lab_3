package ru.itmo.students.exception;

import ru.itmo.students.dto.ErrorDetail;

import java.util.List;

/**
 * 409: значение уже занято (ИСУ, логин, почта).
 */
public class ConflictException extends ApiException {

    public ConflictException(String field, String message) {
        super(409, "CONFLICT", message, List.of(new ErrorDetail(field, message)));
    }
}
