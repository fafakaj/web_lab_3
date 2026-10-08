package ru.itmo.students.security;

import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Хеширование паролей алгоритмом BCrypt.
 * BCrypt сам создаёт случайную соль и хранит её внутри хеша,
 * поэтому у двух одинаковых паролей хеши разные.
 */
@ApplicationScoped
public class PasswordHasher {

    // Стоимость 12: 2^12 раундов. Чем больше, тем медленнее перебор паролей
    private static final int COST = 12;

    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(COST));
    }

    public boolean matches(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
