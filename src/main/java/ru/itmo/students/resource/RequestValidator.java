package ru.itmo.students.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import ru.itmo.students.dto.ErrorDetail;
import ru.itmo.students.exception.DataValidationException;
import ru.itmo.students.exception.InvalidRequestException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Проверяет DTO по аннотациям Bean Validation (@NotNull, @Pattern, @Size).
 * Все нарушения собираются в один ответ 422: клиент сразу видит все ошибочные поля.
 */
@ApplicationScoped
public class RequestValidator {

    @Inject
    Validator validator;

    public void validate(Object body) {
        if (body == null) {
            throw new InvalidRequestException("Нет тела запроса");
        }

        Set<ConstraintViolation<Object>> violations = validator.validate(body);
        if (violations.isEmpty()) {
            return;
        }

        // Одно сообщение на поле: если не прошли и @Size, и @Pattern, показываем одно
        Map<String, String> messages = new LinkedHashMap<>();
        for (ConstraintViolation<Object> violation : violations) {
            messages.putIfAbsent(fieldName(violation.getPropertyPath()), violation.getMessage());
        }

        List<ErrorDetail> details = new ArrayList<>();
        messages.forEach((field, message) -> details.add(new ErrorDetail(field, message)));
        details.sort((a, b) -> a.field().compareTo(b.field()));
        throw new DataValidationException(details);
    }

    /** Имя поля Java -> имя поля в JSON: fullName -> full_name. */
    private static String fieldName(Path path) {
        String name = "";
        for (Path.Node node : path) {
            name = node.getName();
        }
        return name.replaceAll("([A-Z])", "_$1").toLowerCase();
    }
}
