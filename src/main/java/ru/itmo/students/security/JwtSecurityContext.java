package ru.itmo.students.security;

import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;

/**
 * Кто делает запрос: логин и роль из access-токена.
 * AuthenticationFilter кладёт этот объект в запрос, ресурсы и AuthorizationFilter его читают.
 */
public class JwtSecurityContext implements SecurityContext {

    private final String username;
    private final String role;
    private final boolean secure;

    public JwtSecurityContext(String username, String role, boolean secure) {
        this.username = username;
        this.role = role;
        this.secure = secure;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> username;
    }

    @Override
    public boolean isUserInRole(String role) {
        return this.role.equals(role);
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }
}
