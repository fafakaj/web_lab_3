package ru.itmo.students.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import ru.itmo.students.config.JsonConfig;
import ru.itmo.students.dto.ErrorDetail;
import ru.itmo.students.dto.PageResponse;
import ru.itmo.students.dto.StudentFilter;
import ru.itmo.students.dto.StudentQueryParams;
import ru.itmo.students.dto.StudentRequest;
import ru.itmo.students.dto.StudentResponse;
import ru.itmo.students.exception.DataValidationException;
import ru.itmo.students.exception.InvalidRequestException;
import ru.itmo.students.service.StudentService;

import java.io.StringReader;
import java.net.URI;
import java.util.List;

@Path("/requests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"USER", "ADMIN"})
@Tag(name = "Студенты")
@SecurityRequirement(name = "bearerAuth")
public class StudentResource {
    private static final String ISU_PATTERN = "[0-9]{6}";

    @Inject
    StudentService service;

    @Inject
    RequestValidator validator;

    @GET
    @Operation(summary = "Список студентов: поиск по ФИО, фильтры, сортировка, страницы")
    public PageResponse<StudentResponse> list(@BeanParam StudentQueryParams params) {
        List<ErrorDetail> unknown = params.findUnknownParams();
        if (!unknown.isEmpty()) {
            throw new DataValidationException(unknown);
        }
        validator.validate(params);

        StudentFilter filter = params.toFilter();
        validator.validate(filter);
        return service.find(filter);
    }

    @QUERY
    @Operation(hidden = true)
    public PageResponse<StudentResponse> query(StudentFilter filter) {
        validator.validate(filter);
        return service.find(filter);
    }

    @GET
    @Path("/{isu_id}")
    @Operation(summary = "Один студент по ИСУ")
    public StudentResponse get(@PathParam("isu_id") String isuId) {
        checkIsu(isuId);
        return service.get(isuId);
    }

    @POST
    @RolesAllowed("ADMIN")
    @Operation(summary = "Создать студента (только ADMIN)")
    public Response create(StudentRequest request, @Context UriInfo uriInfo) {
        validator.validate(request);
        StudentResponse created = service.create(request);

        URI location = uriInfo.getAbsolutePathBuilder().path(created.isuId()).build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{isu_id}")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Полностью заменить данные студента (только ADMIN)")
    public StudentResponse replace(@PathParam("isu_id") String isuId, StudentRequest request) {
        checkIsu(isuId);
        validator.validate(request);
        return service.replace(isuId, request);
    }

    @PATCH
    @Path("/{isu_id}")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Частично изменить студента (только ADMIN)")
    public StudentResponse patch(@PathParam("isu_id") String isuId, JsonValue body) {
        checkIsu(isuId);
        if (body == null || body.getValueType() != JsonValue.ValueType.OBJECT) {
            throw new InvalidRequestException("Тело запроса должно быть JSON-объектом с изменяемыми полями");
        }
        JsonObject changes = body.asJsonObject();
        if (changes.isEmpty()) {
            throw new InvalidRequestException("Нет полей для обновления");
        }

        StudentResponse current = service.get(isuId);
        JsonObject currentJson = Json.createReader(new StringReader(JsonConfig.JSONB.toJson(current))).readObject();
        JsonValue merged = Json.createMergePatch(changes).apply(currentJson);

        StudentRequest request = JsonConfig.JSONB.fromJson(merged.toString(), StudentRequest.class);
        validator.validate(request);
        return service.replace(isuId, request);
    }

    @DELETE
    @Path("/{isu_id}")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Удалить студента (только ADMIN)")
    public Response delete(@PathParam("isu_id") String isuId) {
        checkIsu(isuId);
        service.delete(isuId);
        return Response.noContent().build();
    }

    private static void checkIsu(String isuId) {
        if (!isuId.matches(ISU_PATTERN)) {
            throw new DataValidationException("isu_id", "ИСУ — ровно 6 цифр, например 412345");
        }
    }
}
