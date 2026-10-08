package ru.itmo.students.resource;

import jakarta.ws.rs.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * HTTP-метод QUERY из лабораторной работы №2: как GET, но фильтры передаются в теле запроса.
 * В Jakarta REST есть только @GET, @POST, @PUT, @PATCH, @DELETE, @HEAD, @OPTIONS.
 * Свой метод объявляется так же, как они: аннотация с @HttpMethod("ИМЯ").
 * RUNTIME — чтобы Jakarta REST увидел аннотацию через рефлексию во время работы.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("QUERY")
public @interface QUERY {
}
