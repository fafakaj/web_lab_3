package ru.itmo.students.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.ext.Provider;

import java.security.Principal;
import java.util.logging.Logger;

/**
 * Пишет в лог каждый запрос к API: метод, адрес, статус ответа, время и пользователя.
 * Пример строки: GET /api/requests?page=1 -> 200 (12 мс, пользователь: admin)
 */
@Provider
@PreMatching
public class RequestLogFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(RequestLogFilter.class.getName());
    private static final String START_TIME = "request-start-time";

    @Override
    public void filter(ContainerRequestContext request) {
        request.setProperty(START_TIME, System.nanoTime());
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        Object start = request.getProperty(START_TIME);
        long millis = start == null ? 0 : (System.nanoTime() - (long) start) / 1_000_000;

        Principal user = request.getSecurityContext().getUserPrincipal();
        String username = user == null ? "-" : user.getName();

        String path = request.getUriInfo().getRequestUri().getRawPath();
        String query = request.getUriInfo().getRequestUri().getRawQuery();
        if (query != null) {
            path += "?" + query;
        }

        LOG.info(String.format("%s %s -> %d (%d мс, пользователь: %s)",
                request.getMethod(), path, response.getStatus(), millis, username));
    }
}
