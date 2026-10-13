package ru.itmo.students.exception;

import ru.itmo.students.dto.ErrorDetail;

import java.util.List;

public class DataValidationException extends ApiException {
    public DataValidationException(List<ErrorDetail> details) {
        super(422, "VALIDATION_ERROR", "Данные не прошли проверку", details);
    }

    public DataValidationException(String field, String message) {
        this(List.of(new ErrorDetail(field, message)));
    }
}
