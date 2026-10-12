package ru.itmo.students.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.itmo.students.dto.RegisterRequest;
import ru.itmo.students.security.JwtService;
import ru.itmo.students.service.AuthService;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Действия при старте приложения:
 * 1) проверить секрет JWT: с плохим JWT_SECRET приложение не запустится;
 * 2) создать администратора из переменных ADMIN_* (.env), если его ещё нет.
 * Метод вызывается событием @Initialized(ApplicationScoped.class): к этому моменту контейнер готов,
 * и @Transactional в сервисах уже работает (в @PostConstruct прокси ещё могло не быть).
 */
@ApplicationScoped
public class StartupData {

    private static final Logger LOG = Logger.getLogger(StartupData.class.getName());

    @Inject
    AuthService authService;

    @Inject
    JwtService jwtService;

    @Inject
    Validator validator;

    @Inject
    @ConfigProperty(name = "ADMIN_USERNAME")
    String adminUsername;

    @Inject
    @ConfigProperty(name = "ADMIN_EMAIL")
    String adminEmail;

    @Inject
    @ConfigProperty(name = "ADMIN_PASSWORD")
    String adminPassword;

    void onStart(@Observes @Initialized(ApplicationScoped.class) Object event) {
        // CDI создаёт бин при первом вызове. Вызываем сейчас: проверка секрета в JwtService.init()
        // остановит запуск, а не сломает первый вход пользователя
        jwtService.getAccessTtl();

        try {
            createAdmin();
        } catch (RuntimeException e) {
            LOG.severe("Приложение не смогло подготовить данные при старте. Проверьте, что база данных запущена "
                    + "и что настройки DB_* и ADMIN_* в файле .env верные. Причина: " + e);
            throw e;
        }
    }

    private void createAdmin() {
        RegisterRequest admin = new RegisterRequest(adminUsername, adminEmail, adminPassword);

        // Плохой пароль администратора в .env — ошибка настройки: лучше не запуститься, чем работать с ним
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(admin);
        if (!violations.isEmpty()) {
            ConstraintViolation<RegisterRequest> first = violations.iterator().next();
            throw new IllegalStateException("ADMIN_* в .env: " + first.getPropertyPath() + " — " + first.getMessage());
        }
        authService.createAdminIfMissing(admin);
    }

}
