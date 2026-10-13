package ru.itmo.students.dto;

import java.util.List;

public record ErrorResponse(Body error) {
    public record Body(int status, String code, String message, List<ErrorDetail> details) {
    }

    public static ErrorResponse of(int status, String code, String message, List<ErrorDetail> details) {
        return new ErrorResponse(new Body(status, code, message, details));
    }
}
