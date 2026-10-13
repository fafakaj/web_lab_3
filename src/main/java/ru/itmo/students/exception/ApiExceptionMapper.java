package ru.itmo.students.exception;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.students.dto.ErrorResponse;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<ApiException> {
    @Override
    public Response toResponse(ApiException e) {
        ErrorResponse body = ErrorResponse.of(e.getStatus(), e.getCode(), e.getMessage(), e.getDetails());
        Response.ResponseBuilder response = Response.status(e.getStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(body);

        if (e.getStatus() == 401) {
            response.header(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        }
        return response.build();
    }
}
