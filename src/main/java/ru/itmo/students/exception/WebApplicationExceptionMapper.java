package ru.itmo.students.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.students.dto.ErrorResponse;

import java.util.List;

/**
 * Ошибки, которые бросает сам Jakarta REST до вызова нашего кода:
 * неизвестный адрес, неподходящий метод, не тот Content-Type, битый JSON.
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException e) {
        int status = e.getResponse().getStatus();
        String code;
        String message;

        switch (status) {
            case 400 -> {
                code = "BAD_REQUEST";
                message = "Тело запроса — некорректный JSON или поле неверного типа";
            }
            case 404 -> {
                code = "NOT_FOUND";
                message = "Адрес не найден";
            }
            case 405 -> {
                code = "METHOD_NOT_ALLOWED";
                message = "Метод не поддерживается для этого адреса";
            }
            case 406 -> {
                code = "NOT_ACCEPTABLE";
                message = "Сервер отвечает только в формате application/json";
            }
            case 415 -> {
                code = "UNSUPPORTED_MEDIA_TYPE";
                message = "Тело запроса должно быть JSON с заголовком Content-Type: application/json";
            }
            default -> {
                code = "HTTP_ERROR";
                message = "Ошибка запроса";
            }
        }

        Response.ResponseBuilder response = Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorResponse.of(status, code, message, List.of()));

        // Для 405 оставляем заголовок Allow: в нём список разрешённых методов
        String allow = e.getResponse().getHeaderString("Allow");
        if (allow != null) {
            response.header("Allow", allow);
        }
        return response.build();
    }
}
