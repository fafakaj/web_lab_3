package ru.itmo.students.exception;

import java.util.List;

/**
 * 403: пользователь известен, но его роли не хватает прав.
 */
public class AccessDeniedException extends ApiException {

    public AccessDeniedException(String message) {
        super(403, "FORBIDDEN", message, List.of());
    }
}
