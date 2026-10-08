package ru.itmo.students.config;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import java.lang.reflect.Type;

/**
 * Настройка JSON-B (объект <-> JSON) для всего API.
 * По умолчанию JSON-B «мягкий»: число 412345 молча станет строкой "412345",
 * "yes" — значением false, а лишние поля просто пропадут. Как в лабораторной работе №2,
 * такие данные надо отклонять, поэтому здесь:
 * - строки принимаются только из JSON-строк, логические значения — только из true/false;
 * - неизвестное поле — ошибка.
 */
@Provider
public class JsonConfig implements ContextResolver<Jsonb> {

    public static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig()
            .withDeserializers(new StrictStringDeserializer(), new StrictBooleanDeserializer())
            .setProperty("jsonb.fail-on-unknown-properties", true));

    @Override
    public Jsonb getContext(Class<?> type) {
        return JSONB;
    }

    static class StrictStringDeserializer implements JsonbDeserializer<String> {
        @Override
        public String deserialize(JsonParser parser, DeserializationContext context, Type type) {
            if (parser.currentEvent() != JsonParser.Event.VALUE_STRING) {
                throw new JsonbException("ожидается строка в кавычках");
            }
            return parser.getString();
        }
    }

    static class StrictBooleanDeserializer implements JsonbDeserializer<Boolean> {
        @Override
        public Boolean deserialize(JsonParser parser, DeserializationContext context, Type type) {
            return switch (parser.currentEvent()) {
                case VALUE_TRUE -> true;
                case VALUE_FALSE -> false;
                default -> throw new JsonbException("ожидается true или false");
            };
        }
    }
}
