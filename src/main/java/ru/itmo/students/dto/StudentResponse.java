package ru.itmo.students.dto;

import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbProperty;

import java.time.LocalDate;

public record StudentResponse(
        @JsonbProperty("isu_id") String isuId,
        @JsonbProperty("full_name") String fullName,
        String group,
        String dormitory,
        String room,
        @JsonbProperty("check_in") LocalDate checkIn,

        @JsonbNillable @JsonbProperty("check_out") LocalDate checkOut,
        @JsonbProperty("is_foreign") boolean isForeign,
        String notes
) {
}
