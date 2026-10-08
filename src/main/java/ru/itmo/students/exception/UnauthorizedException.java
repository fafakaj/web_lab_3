package ru.itmo.students.exception;

import java.util.List;

/**
 * 401: неизвестно, кто делает запрос (нет токена, токен неверный или истёк).
 */
public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String message) {
        this("UNAUTHORIZED", message);
    }

    public UnauthorizedException(String code, String message) {
        super(401, code, message, List.of());
    }
}
