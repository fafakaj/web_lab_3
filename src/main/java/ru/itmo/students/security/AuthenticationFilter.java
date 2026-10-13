package ru.itmo.students.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.students.exception.UnauthorizedException;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    @Inject
    JwtService jwtService;

    @Override
    public void filter(ContainerRequestContext request) {
        String header = request.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (header == null) {
            return;
        }

        if (!header.startsWith(BEARER_PREFIX)) {
            throw new UnauthorizedException("Заголовок Authorization должен иметь вид: Bearer <access-токен>");
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        DecodedJWT jwt = jwtService.verify(token, TokenType.ACCESS);

        boolean secure = request.getSecurityContext().isSecure();
        request.setSecurityContext(new JwtSecurityContext(jwt.getSubject(), jwtService.getRole(jwt), secure));
    }
}
