package ru.itmo.students.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ru.itmo.students.entity.RefreshToken;
import ru.itmo.students.entity.User;

import java.util.Optional;

@ApplicationScoped
public class RefreshTokenRepository {
    @PersistenceContext(unitName = "students")
    private EntityManager em;

    public Optional<RefreshToken> findByJti(String jti) {
        return em.createQuery("SELECT t FROM RefreshToken t WHERE t.jti = :jti", RefreshToken.class)
                .setParameter("jti", jti)
                .getResultStream()
                .findFirst();
    }

    public void save(RefreshToken token) {
        em.persist(token);
    }

    public void revokeAllOf(User user) {
        em.createQuery("UPDATE RefreshToken t SET t.revoked = true WHERE t.user = :user")
                .setParameter("user", user)
                .executeUpdate();
    }
}
