package ru.itmo.students.exception;

import java.util.List;

public class InvalidRequestException extends ApiException {
    public InvalidRequestException(String message) {
        super(400, "BAD_REQUEST", message, List.of());
    }
}
