package ru.itmo.students.exception;

import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.students.dto.ErrorDetail;
import ru.itmo.students.dto.ErrorResponse;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ошибка при чтении JSON-тела.
 * - Поле неверного типа ("is_foreign": "yes") или неизвестное поле -> 422 с именем поля.
 * - Тело вообще не JSON-объект (битый JSON, массив, пустое тело) -> 400.
 * Имя поля JSON-B (реализация Yasson) пишет в тексте ошибки, его достаём регулярным выражением.
 */
@Provider
public class JsonbExceptionMapper implements ExceptionMapper<JsonbException> {

    private static final Pattern BAD_PROPERTY = Pattern.compile("deserialize property '([A-Za-z_]+)'");
    private static final Pattern UNKNOWN_PROPERTY = Pattern.compile("Json property ([A-Za-z0-9_]+) can not be mapped");

    @Override
    public Response toResponse(JsonbException e) {
        String message = String.valueOf(e.getMessage());

        Matcher unknown = UNKNOWN_PROPERTY.matcher(message);
        if (unknown.find()) {
            return validationError(new ErrorDetail(unknown.group(1), "Неизвестное поле"));
        }

        Matcher bad = BAD_PROPERTY.matcher(message);
        if (bad.find()) {
            return validationError(new ErrorDetail(bad.group(1), "Неверный тип или формат значения"));
        }

        ErrorResponse body = ErrorResponse.of(400, "BAD_REQUEST",
                "Тело запроса должно быть JSON-объектом, например {\"group\": \"M3301\"}", List.of());
        return Response.status(400).type(MediaType.APPLICATION_JSON).entity(body).build();
    }

    private static Response validationError(ErrorDetail detail) {
        ErrorResponse body = ErrorResponse.of(422, "VALIDATION_ERROR", "Данные не прошли проверку", List.of(detail));
        return Response.status(422).type(MediaType.APPLICATION_JSON).entity(body).build();
    }
}
