package ru.itmo.students.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ru.itmo.students.entity.User;

import java.util.Optional;

/**
 * Доступ к таблице users.
 */
@ApplicationScoped
public class UserRepository {

    @PersistenceContext(unitName = "students")
    private EntityManager em;

    public Optional<User> findByUsername(String username) {
        return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    public void save(User user) {
        em.persist(user);
        // id выдаёт сама БД (IDENTITY), поэтому INSERT выполняем сразу, чтобы id появился в объекте
        em.flush();
    }
}
