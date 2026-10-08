package ru.itmo.students.resource;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import ru.itmo.students.dto.LoginRequest;
import ru.itmo.students.dto.RefreshRequest;
import ru.itmo.students.dto.RegisterRequest;
import ru.itmo.students.dto.TokenResponse;
import ru.itmo.students.dto.UserResponse;
import ru.itmo.students.service.AuthService;

/**
 * Регистрация, вход и токены. Эти адреса открыты всем (@PermitAll), кроме /me.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
@Tag(name = "Авторизация")
public class AuthResource {

    @Inject
    AuthService service;

    @Inject
    RequestValidator validator;

    @POST
    @Path("/register")
    @Operation(summary = "Регистрация. Новый пользователь получает роль USER")
    public Response register(RegisterRequest request) {
        validator.validate(request);
        UserResponse user = service.register(request);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @POST
    @Path("/login")
    @Operation(summary = "Вход по логину и паролю: выдаёт access- и refresh-токены")
    public TokenResponse login(LoginRequest request) {
        validator.validate(request);
        return service.login(request);
    }

    @POST
    @Path("/refresh")
    @Operation(summary = "Новая пара токенов по refresh-токену, без логина и пароля")
    public TokenResponse refresh(RefreshRequest request) {
        validator.validate(request);
        return service.refresh(request.refreshToken());
    }

    @POST
    @Path("/logout")
    @Operation(summary = "Выход: refresh-токен отзывается")
    public Response logout(RefreshRequest request) {
        validator.validate(request);
        service.logout(request.refreshToken());
        return Response.noContent().build();
    }

    @GET
    @Path("/me")
    @RolesAllowed({"USER", "ADMIN"})
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Кто я: данные пользователя из access-токена")
    public UserResponse me(@Context SecurityContext security) {
        return service.getUser(security.getUserPrincipal().getName());
    }
}
