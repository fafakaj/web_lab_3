package ru.itmo.students.exception;

import java.util.List;

/**
 * 404: запись не найдена.
 */
public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String message) {
        super(404, "NOT_FOUND", message, List.of());
    }
}
