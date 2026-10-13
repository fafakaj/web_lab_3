package ru.itmo.students.security;

import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class PasswordHasher {
    private static final int COST = 12;

    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(COST));
    }

    public boolean matches(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
