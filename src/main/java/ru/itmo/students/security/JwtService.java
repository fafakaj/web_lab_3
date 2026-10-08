package ru.itmo.students.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.itmo.students.entity.User;
import ru.itmo.students.exception.UnauthorizedException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

/**
 * Выпуск и проверка JWT.
 * Токен подписан алгоритмом HS256 секретом JWT_SECRET из окружения:
 * кто не знает секрет, не может подделать токен.
 */
@ApplicationScoped
public class JwtService {

    private static final String ISSUER = "web_lab_3";
    private static final String ROLE_CLAIM = "role";
    private static final String TYPE_CLAIM = "type";

    @Inject
    @ConfigProperty(name = "JWT_SECRET")
    String secret;

    @Inject
    @ConfigProperty(name = "JWT_ACCESS_MINUTES", defaultValue = "15")
    long accessMinutes;

    @Inject
    @ConfigProperty(name = "JWT_REFRESH_DAYS", defaultValue = "7")
    long refreshDays;

    private Algorithm algorithm;
    private JWTVerifier verifier;

    @PostConstruct
    void init() {
        // HS256 требует ключ не короче 256 бит = 32 байта
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET должен быть не короче 32 символов");
        }
        algorithm = Algorithm.HMAC256(secret);
        verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
    }

    public String createAccessToken(User user) {
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.getUsername())
                .withClaim(ROLE_CLAIM, user.getRole().name())
                .withClaim(TYPE_CLAIM, TokenType.ACCESS.name())
                .withIssuedAt(now)
                .withExpiresAt(now.plus(getAccessTtl()))
                .sign(algorithm);
    }

    /** jti — уникальный номер токена; по нему refresh-токен ищется в БД. */
    public String createRefreshToken(User user, String jti, Instant expiresAt) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.getUsername())
                .withJWTId(jti)
                .withClaim(TYPE_CLAIM, TokenType.REFRESH.name())
                .withIssuedAt(Instant.now())
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    /**
     * Проверяет подпись, издателя, срок действия и тип токена.
     * При любой ошибке бросает UnauthorizedException (ответ 401).
     */
    public DecodedJWT verify(String token, TokenType expectedType) {
        DecodedJWT jwt;
        try {
            jwt = verifier.verify(token);
        } catch (TokenExpiredException e) {
            throw new UnauthorizedException("TOKEN_EXPIRED", "Срок действия токена истёк");
        } catch (JWTVerificationException e) {
            throw new UnauthorizedException("INVALID_TOKEN", "Токен недействителен");
        }

        if (!expectedType.name().equals(jwt.getClaim(TYPE_CLAIM).asString())) {
            throw new UnauthorizedException("INVALID_TOKEN", "Неверный тип токена");
        }
        return jwt;
    }

    public String getRole(DecodedJWT jwt) {
        return jwt.getClaim(ROLE_CLAIM).asString();
    }

    public Duration getAccessTtl() {
        return Duration.ofMinutes(accessMinutes);
    }

    public Duration getRefreshTtl() {
        return Duration.ofDays(refreshDays);
    }
}
