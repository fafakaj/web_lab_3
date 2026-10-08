package ru.itmo.students.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Query-параметры GET /api/requests?group=M3301&page=2.
 * В адресе всё — строки, поэтому здесь проверяется только, что строку можно перевести
 * в число или true/false. Остальные правила — в StudentFilter.
 */
public class StudentQueryParams {

    private static final Set<String> KNOWN_NAMES = Set.of(
            "full_name", "group", "dormitory", "room", "is_foreign", "living", "sort", "order", "page", "size");

    @QueryParam("full_name")
    String fullName;

    @QueryParam("group")
    String group;

    @QueryParam("dormitory")
    String dormitory;

    @QueryParam("room")
    String room;

    @QueryParam("is_foreign")
    @Pattern(regexp = "true|false", message = "Иностранец — true или false")
    String isForeign;

    @QueryParam("living")
    @Pattern(regexp = "true|false", message = "Проживает сейчас — true или false")
    String living;

    @QueryParam("sort")
    String sort;

    @QueryParam("order")
    String order;

    @QueryParam("page")
    @Pattern(regexp = "[0-9]{1,6}", message = "Номер страницы — целое число от 1")
    String page;

    @QueryParam("size")
    @Pattern(regexp = "[0-9]{1,3}", message = "Размер страницы — от 1 до 100")
    String size;

    @Context
    UriInfo uriInfo;

    /** Параметры, которых API не знает (например, ?foo=1). Опечатка не должна молча игнорироваться. */
    public List<ErrorDetail> findUnknownParams() {
        List<ErrorDetail> unknown = new ArrayList<>();
        for (String name : uriInfo.getQueryParameters().keySet()) {
            if (!KNOWN_NAMES.contains(name)) {
                unknown.add(new ErrorDetail(name, "Неизвестный параметр"));
            }
        }
        return unknown;
    }

    public StudentFilter toFilter() {
        return new StudentFilter(fullName, group, dormitory, room,
                toBoolean(isForeign), toBoolean(living), sort, order, toInteger(page), toInteger(size));
    }

    private static Boolean toBoolean(String value) {
        return value == null ? null : Boolean.valueOf(value);
    }

    private static Integer toInteger(String value) {
        return value == null ? null : Integer.valueOf(value);
    }
}
