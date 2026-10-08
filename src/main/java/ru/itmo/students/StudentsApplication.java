package ru.itmo.students;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

/**
 * Точка входа Jakarta REST: все ресурсы доступны по адресам /api/...
 * Аннотации OpenAPI описывают API для Swagger UI (/openapi/ui) и кнопку Authorize для JWT.
 */
@ApplicationPath("/api")
@OpenAPIDefinition(info = @Info(
        title = "Система управления студентами общежития",
        version = "3.0",
        description = "Лабораторная работа №3: Jakarta EE + EclipseLink, JWT, роли ADMIN и USER. "
                + "Войдите через POST /api/auth/login, скопируйте access_token и нажмите Authorize."))
@SecurityScheme(securitySchemeName = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class StudentsApplication extends Application {
}
