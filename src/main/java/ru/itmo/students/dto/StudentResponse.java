package ru.itmo.students.dto;

import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbProperty;

import java.time.LocalDate;

/**
 * Студент в ответе API. Отделяет внешний формат JSON от сущности Student.
 */
public record StudentResponse(
        @JsonbProperty("isu_id") String isuId,
        @JsonbProperty("full_name") String fullName,
        String group,
        String dormitory,
        String room,
        @JsonbProperty("check_in") LocalDate checkIn,
        // @JsonbNillable: без неё JSON-B пропустил бы поле, а клиенту нужно явное "check_out": null
        @JsonbNillable @JsonbProperty("check_out") LocalDate checkOut,
        @JsonbProperty("is_foreign") boolean isForeign,
        String notes
) {
}
