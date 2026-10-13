package ru.itmo.students.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StudentFilter(

        @JsonbProperty("full_name")
        @Size(min = 2, max = 100, message = StudentFilter.NAME_HINT)
        @Pattern(regexp = "[A-Za-zА-ЯЁа-яё' \\-]+", message = StudentFilter.NAME_HINT)
        String fullName,

        @Pattern(regexp = "[A-Z][0-9]{4}", message = "Группа — латинская буква и 4 цифры, например M3301")
        String group,

        @Pattern(regexp = "sg|alp|bel|len|msg", message = "Общежитие — один из кодов: sg, alp, bel, len, msg")
        String dormitory,

        @Pattern(regexp = "[1-9][0-9]{3}", message = "Комната — ровно 4 цифры, первая не 0, например 1205")
        String room,

        @JsonbProperty("is_foreign")
        Boolean isForeign,

        Boolean living,

        @Pattern(regexp = "isu_id|full_name|group|dormitory|room|check_in|check_out",
                message = "Сортировка — одно из полей: isu_id, full_name, group, dormitory, room, check_in, check_out")
        String sort,

        @Pattern(regexp = "asc|desc", message = "Порядок — asc (по возрастанию) или desc (по убыванию)")
        String order,

        @Min(value = 1, message = "Номер страницы — целое число от 1")
        Integer page,

        @Min(value = 1, message = "Размер страницы — от 1 до 100")
        @Max(value = 100, message = "Размер страницы — от 1 до 100")
        Integer size
) {
    static final String NAME_HINT = "ФИО в фильтре — от 2 до 100 символов: буквы, пробел, дефис или апостроф";

    public StudentFilter {
        fullName = StudentRequest.collapseSpaces(fullName);
        group = group == null ? null : group.strip().toUpperCase();
        sort = sort == null ? "full_name" : sort;
        order = order == null ? "asc" : order;
        page = page == null ? 1 : page;
        size = size == null ? 10 : size;
    }
}
