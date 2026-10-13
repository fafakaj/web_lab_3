package ru.itmo.students.exception;

import java.util.List;

public class AccessDeniedException extends ApiException {
    public AccessDeniedException(String message) {
        super(403, "FORBIDDEN", message, List.of());
    }
}
