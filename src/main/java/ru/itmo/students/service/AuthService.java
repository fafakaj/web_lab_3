package ru.itmo.students.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.itmo.students.dto.LoginRequest;
import ru.itmo.students.dto.RegisterRequest;
import ru.itmo.students.dto.TokenResponse;
import ru.itmo.students.dto.UserResponse;
import ru.itmo.students.entity.RefreshToken;
import ru.itmo.students.entity.Role;
import ru.itmo.students.entity.User;
import ru.itmo.students.exception.ConflictException;
import ru.itmo.students.exception.DataValidationException;
import ru.itmo.students.exception.UnauthorizedException;
import ru.itmo.students.repository.RefreshTokenRepository;
import ru.itmo.students.repository.UserRepository;
import ru.itmo.students.security.JwtService;
import ru.itmo.students.security.PasswordHasher;
import ru.itmo.students.security.TokenType;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.logging.Logger;

@ApplicationScoped
public class AuthService {
    private static final Logger LOG = Logger.getLogger(AuthService.class.getName());

    private static final int BCRYPT_MAX_BYTES = 72;

    @Inject
    UserRepository users;

    @Inject
    RefreshTokenRepository refreshTokens;

    @Inject
    PasswordHasher passwordHasher;

    @Inject
    JwtService jwtService;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        User user = createUser(request, Role.USER);
        LOG.info("Зарегистрирован пользователь " + user.getUsername());
        return toResponse(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = users.findByUsername(request.username())
                .filter(found -> passwordHasher.matches(request.password(), found.getPasswordHash()))
                .orElse(null);

        if (user == null) {
            LOG.warning("Неудачный вход: " + request.username());
            throw new UnauthorizedException("Неверный логин или пароль");
        }

        LOG.info("Вход пользователя " + user.getUsername());
        return issueTokens(user);
    }

    @Transactional(dontRollbackOn = UnauthorizedException.class)
    public TokenResponse refresh(String token) {
        RefreshToken stored = findRefreshToken(token);

        if (stored.isRevoked()) {
            refreshTokens.revokeAllOf(stored.getUser());
            LOG.warning("Повторное использование refresh-токена, все токены отозваны: " + stored.getUser().getUsername());
            throw new UnauthorizedException("INVALID_TOKEN", "Refresh-токен уже использован или отозван. Войдите заново");
        }

        stored.setRevoked(true);
        return issueTokens(stored.getUser());
    }

    @Transactional
    public void logout(String token) {
        RefreshToken stored = findRefreshToken(token);
        stored.setRevoked(true);
        LOG.info("Выход пользователя " + stored.getUser().getUsername());
    }

    public UserResponse getUser(String username) {
        return users.findByUsername(username)
                .map(AuthService::toResponse)
                .orElseThrow(() -> new UnauthorizedException("Пользователь не найден"));
    }

    @Transactional
    public void createAdminIfMissing(RegisterRequest admin) {
        if (users.existsByUsername(admin.username())) {
            return;
        }
        createUser(admin, Role.ADMIN);
        LOG.info("Создан администратор " + admin.username());
    }

    private User createUser(RegisterRequest request, Role role) {
        if (request.password().getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_BYTES) {
            throw new DataValidationException("password", "Пароль слишком длинный: не больше 72 байт (около 36 русских букв)");
        }
        if (users.existsByUsername(request.username())) {
            throw new ConflictException("username", "Логин " + request.username() + " уже занят");
        }
        if (users.existsByEmail(request.email())) {
            throw new ConflictException("email", "Почта " + request.email() + " уже используется");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordHasher.hash(request.password()));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        users.save(user);
        return user;
    }

    private RefreshToken findRefreshToken(String token) {
        DecodedJWT jwt = jwtService.verify(token, TokenType.REFRESH);
        return refreshTokens.findByJti(jwt.getId())
                .orElseThrow(() -> new UnauthorizedException("INVALID_TOKEN", "Refresh-токен не найден"));
    }

    private TokenResponse issueTokens(User user) {
        String jti = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(jwtService.getRefreshTtl());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setJti(jti);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.ofInstant(expiresAt, ZoneOffset.UTC));
        refreshTokens.save(refreshToken);

        return new TokenResponse(
                jwtService.createAccessToken(user),
                jwtService.createRefreshToken(user, jti, expiresAt),
                "Bearer",
                jwtService.getAccessTtl().toSeconds(),
                toResponse(user));
    }

    private static UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }
}
