package ru.itmo.students.exception;

import ru.itmo.students.dto.ErrorDetail;

import java.util.List;

public abstract class ApiException extends RuntimeException {
    private final int status;
    private final String code;
    private final List<ErrorDetail> details;

    protected ApiException(int status, String code, String message, List<ErrorDetail> details) {
        super(message);
        this.status = status;
        this.code = code;
        this.details = details;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public List<ErrorDetail> getDetails() {
        return details;
    }
}
