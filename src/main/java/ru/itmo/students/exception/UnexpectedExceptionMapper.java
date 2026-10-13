package ru.itmo.students.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.students.dto.ErrorResponse;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class UnexpectedExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOG = Logger.getLogger(UnexpectedExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable e) {
        LOG.log(Level.SEVERE, "Необработанная ошибка", e);

        ErrorResponse body = ErrorResponse.of(500, "INTERNAL_ERROR",
                "Внутренняя ошибка сервера. Подробности записаны в лог сервера", List.of());
        return Response.status(500).type(MediaType.APPLICATION_JSON).entity(body).build();
    }
}
