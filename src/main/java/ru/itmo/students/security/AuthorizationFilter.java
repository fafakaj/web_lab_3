package ru.itmo.students.security;

import jakarta.annotation.Priority;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.students.exception.AccessDeniedException;
import ru.itmo.students.exception.UnauthorizedException;

import java.lang.reflect.Method;

/**
 * Авторизация — «что ему можно?».
 * Работает после выбора метода ресурса и читает его аннотации через рефлексию:
 * - @PermitAll — пускать всех, даже без токена;
 * - @RolesAllowed({"ADMIN"}) — пускать только эти роли.
 * Аннотация на методе важнее аннотации на классе. Нет ни одной — нужен любой вошедший пользователь.
 * Без токена — 401, токен есть, но роль не та — 403.
 */
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext request) {
        Method method = resourceInfo.getResourceMethod();
        Class<?> resourceClass = resourceInfo.getResourceClass();

        if (method.isAnnotationPresent(PermitAll.class)) {
            return;
        }

        RolesAllowed rolesAllowed = method.getAnnotation(RolesAllowed.class);
        if (rolesAllowed == null) {
            rolesAllowed = resourceClass.getAnnotation(RolesAllowed.class);
        }

        if (rolesAllowed == null && resourceClass.isAnnotationPresent(PermitAll.class)) {
            return;
        }

        SecurityContext security = request.getSecurityContext();

        if (security.getUserPrincipal() == null) {
            throw new UnauthorizedException("Нужно войти: передайте заголовок Authorization: Bearer <access-токен>");
        }

        if (rolesAllowed == null) {
            return;
        }

        for (String role : rolesAllowed.value()) {
            if (security.isUserInRole(role)) {
                return;
            }
        }

        throw new AccessDeniedException("Недостаточно прав: операция доступна только ролям " + String.join(", ", rolesAllowed.value()));
    }
}
