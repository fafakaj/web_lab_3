package ru.itmo.students.exception;

import ru.itmo.students.dto.ErrorDetail;

import java.util.List;

public class ConflictException extends ApiException {
    public ConflictException(String field, String message) {
        super(409, "CONFLICT", message, List.of(new ErrorDetail(field, message)));
    }
}
