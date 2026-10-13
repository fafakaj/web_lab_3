package ru.itmo.students.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import ru.itmo.students.dto.StudentFilter;
import ru.itmo.students.entity.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class StudentRepository {
    private static final Map<String, String> SORT_FIELDS = Map.of(
            "isu_id", "s.isuId",
            "full_name", "s.fullName",
            "group", "s.group",
            "dormitory", "s.dormitory",
            "room", "s.room",
            "check_in", "s.checkIn",
            "check_out", "s.checkOut");

    @PersistenceContext(unitName = "students")
    private EntityManager em;

    public Optional<Student> findById(String isuId) {
        return Optional.ofNullable(em.find(Student.class, isuId));
    }

    public boolean existsById(String isuId) {
        return em.find(Student.class, isuId) != null;
    }

    public void save(Student student) {
        em.persist(student);
    }

    public void delete(Student student) {
        em.remove(student);
    }

    public List<Student> find(StudentFilter filter) {
        Map<String, Object> params = new HashMap<>();
        String orderBy = SORT_FIELDS.get(filter.sort()) + ("desc".equals(filter.order()) ? " DESC" : " ASC");

        String jpql = "SELECT s FROM Student s" + where(filter, params) + " ORDER BY " + orderBy + ", s.isuId";

        TypedQuery<Student> query = em.createQuery(jpql, Student.class);
        params.forEach(query::setParameter);
        return query
                .setFirstResult((filter.page() - 1) * filter.size())
                .setMaxResults(filter.size())
                .getResultList();
    }

    public long count(StudentFilter filter) {
        Map<String, Object> params = new HashMap<>();
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(s) FROM Student s" + where(filter, params), Long.class);
        params.forEach(query::setParameter);
        return query.getSingleResult();
    }

    private static String where(StudentFilter filter, Map<String, Object> params) {
        List<String> conditions = new ArrayList<>();

        if (filter.fullName() != null) {
            conditions.add("LOWER(s.fullName) LIKE :fullName");
            params.put("fullName", "%" + filter.fullName().toLowerCase() + "%");
        }
        if (filter.group() != null) {
            conditions.add("s.group = :group");
            params.put("group", filter.group());
        }
        if (filter.dormitory() != null) {
            conditions.add("s.dormitory = :dormitory");
            params.put("dormitory", filter.dormitory());
        }
        if (filter.room() != null) {
            conditions.add("s.room = :room");
            params.put("room", filter.room());
        }
        if (filter.isForeign() != null) {
            conditions.add("s.foreign = :foreign");
            params.put("foreign", filter.isForeign());
        }
        if (filter.living() != null) {
            conditions.add(filter.living() ? "s.checkOut IS NULL" : "s.checkOut IS NOT NULL");
        }

        return conditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", conditions);
    }
}
