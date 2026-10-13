package ru.itmo.students.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record StudentRequest(
        @JsonbProperty("isu_id")
        @Pattern(regexp = "[0-9]{6}", message = StudentRequest.ISU_HINT)
        String isuId,

        @JsonbProperty("full_name")
        @NotNull(message = StudentRequest.REQUIRED)
        @Size(min = 5, max = 100, message = StudentRequest.FULL_NAME_HINT)
        @Pattern(regexp = StudentRequest.FULL_NAME_REGEX, message = StudentRequest.FULL_NAME_HINT)
        String fullName,

        @NotNull(message = StudentRequest.REQUIRED)
        @Pattern(regexp = "[A-Z][0-9]{4}", message = "Группа — латинская буква и 4 цифры, например M3301")
        String group,

        @NotNull(message = StudentRequest.REQUIRED)
        @Pattern(regexp = "sg|alp|bel|len|msg", message = "Общежитие — один из кодов: sg, alp, bel, len, msg")
        String dormitory,

        @NotNull(message = StudentRequest.REQUIRED)
        @Pattern(regexp = "[1-9][0-9]{3}", message = "Комната — ровно 4 цифры, первая не 0, например 1205")
        String room,

        @JsonbProperty("check_in")
        @NotNull(message = StudentRequest.REQUIRED)
        LocalDate checkIn,

        @JsonbProperty("check_out")
        LocalDate checkOut,

        @JsonbProperty("is_foreign")
        @NotNull(message = StudentRequest.REQUIRED)
        Boolean isForeign,

        @Size(max = 500, message = "Заметки — не длиннее 500 символов")
        String notes
) {
    static final String REQUIRED = "Обязательное поле";
    static final String ISU_HINT = "ИСУ — строка ровно из 6 цифр, например \"412345\"";
    static final String FULL_NAME_HINT =
            "ФИО — минимум два слова из букв (внутри слова можно дефис или апостроф), от 5 до 100 символов";

    private static final String WORD = "[A-Za-zА-ЯЁа-яё]+(?:['\\-][A-Za-zА-ЯЁа-яё]+)*";
    static final String FULL_NAME_REGEX = WORD + "(?: " + WORD + ")+";

    public StudentRequest {
        fullName = collapseSpaces(fullName);
        group = group == null ? null : group.strip().toUpperCase();
        notes = notes == null ? "" : notes.strip();
    }

    static String collapseSpaces(String value) {
        return value == null ? null : value.strip().replaceAll("\\s+", " ");
    }
}
