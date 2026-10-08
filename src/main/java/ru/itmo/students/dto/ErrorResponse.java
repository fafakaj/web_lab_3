package ru.itmo.students.dto;

import java.util.List;

/**
 * Единый формат ошибки, как в лабораторной работе №2:
 * {"error": {"status": 404, "code": "NOT_FOUND", "message": "...", "details": [...]}}
 */
public record ErrorResponse(Body error) {

    public record Body(int status, String code, String message, List<ErrorDetail> details) {
    }

    public static ErrorResponse of(int status, String code, String message, List<ErrorDetail> details) {
        return new ErrorResponse(new Body(status, code, message, details));
    }
}
