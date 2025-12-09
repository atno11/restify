package com.atno11.http.utils;

import com.atno11.Restify;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.UUID;

public class ConversionUtils {

    public static Object convertStringToType(String rawValue, Class<?> targetClass, Type genericType) {
        if (rawValue == null) {
            if (targetClass.isPrimitive()) {
                throw new IllegalArgumentException("Parametro primitivo requerido sem valor");
            }
            return null;
        }

        String v = rawValue.trim();

        // Strings
        if (targetClass.equals(String.class)) return v;

        // Primitivos e wrappers
        if (targetClass.equals(Integer.class) || targetClass.equals(int.class)) {
            try { return Integer.valueOf(v); } catch (NumberFormatException e) { throw badNumber(v, "int"); }
        }
        if (targetClass.equals(Long.class) || targetClass.equals(long.class)) {
            try { return Long.valueOf(v); } catch (NumberFormatException e) { throw badNumber(v, "long"); }
        }
        if (targetClass.equals(Short.class) || targetClass.equals(short.class)) {
            try { return Short.valueOf(v); } catch (NumberFormatException e) { throw badNumber(v, "short"); }
        }
        if (targetClass.equals(Byte.class) || targetClass.equals(byte.class)) {
            try { return Byte.valueOf(v); } catch (NumberFormatException e) { throw badNumber(v, "byte"); }
        }
        if (targetClass.equals(Boolean.class) || targetClass.equals(boolean.class)) {
            return Boolean.valueOf(v);
        }
        if (targetClass.equals(Double.class) || targetClass.equals(double.class)) {
            try { return Double.valueOf(v); } catch (NumberFormatException e) { throw badNumber(v, "double"); }
        }
        if (targetClass.equals(Float.class) || targetClass.equals(float.class)) {
            try { return Float.valueOf(v); } catch (NumberFormatException e) { throw badNumber(v, "float"); }
        }

        // Big numbers
        if (targetClass.equals(BigInteger.class)) {
            try { return new BigInteger(v); } catch (NumberFormatException e) { throw badNumber(v, "BigInteger"); }
        }
        if (targetClass.equals(BigDecimal.class)) {
            try { return new BigDecimal(v); } catch (NumberFormatException e) { throw badNumber(v, "BigDecimal"); }
        }

        // UUID
        if (targetClass.equals(UUID.class)) {
            try { return UUID.fromString(v); } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Valor não é um UUID válido: " + v, e);
            }
        }

        // Enums
        if (targetClass.isEnum()) {
            @SuppressWarnings({"rawtypes", "unchecked"})
            Class<? extends Enum> enumClass = (Class<? extends Enum>) targetClass;
            try {
                @SuppressWarnings("unchecked")
                Object enumVal = Enum.valueOf(enumClass, v);
                return enumVal;
            } catch (IllegalArgumentException e) {
                // tenta case-insensitive
                for (Object constant : enumClass.getEnumConstants()) {
                    if (constant.toString().equalsIgnoreCase(v)) return constant;
                }
                throw new IllegalArgumentException("Valor inválido para enum " + enumClass.getSimpleName() + ": " + v);
            }
        }

        // Java Time (ISO formats)
        try {
            if (targetClass.equals(LocalDate.class)) return LocalDate.parse(v);
            if (targetClass.equals(LocalTime.class)) return LocalTime.parse(v);
            if (targetClass.equals(LocalDateTime.class)) return LocalDateTime.parse(v);
            if (targetClass.equals(OffsetDateTime.class)) return OffsetDateTime.parse(v);
            if (targetClass.equals(ZonedDateTime.class)) return ZonedDateTime.parse(v);
            if (targetClass.equals(Instant.class)) return Instant.parse(v);
            if (targetClass.equals(Year.class)) return Year.parse(v);
            if (targetClass.equals(YearMonth.class)) return YearMonth.parse(v);
        } catch (DateTimeException dte) {
            throw new IllegalArgumentException("Formato de data/hora inválido para tipo " + targetClass.getSimpleName() + ": " + v, dte);
        }

        // Arrays / Collections / DTOs / Maps -> tenta desserializar JSON com Gson
        // Se for array/collection/map ou qualquer POJO, confiamos no Gson para criar o objeto.
        try {
            // use genericType se disponível (preserva generics como List<Foo>)
            Type typeToUse = (genericType != null) ? genericType : targetClass;
            Object obj = Restify.GSON.fromJson(v, typeToUse);
            if (obj == null) {
                // Se não conseguiu desserializar e targetClass não é String, pode ser porque a entrada não é JSON.
                // Por exemplo: path variable "42" para um DTO -> tentar converter por construtor/parse?
                // Decisão: lançar erro pra evitar comportamento inesperado.
                throw new IllegalArgumentException("Não foi possível desserializar o valor para " + targetClass.getSimpleName() + ": " + v);
            }
            return obj;
        } catch (JsonSyntaxException jse) {
            throw new IllegalArgumentException("JSON inválido para o tipo " + targetClass.getSimpleName() + ": " + v, jse);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Erro ao converter valor para " + targetClass.getSimpleName() + ": " + ex.getMessage(), ex);
        }
    }

    private static IllegalArgumentException badNumber(String v, String typeName) {
        return new IllegalArgumentException("Valor inválido para " + typeName + ": " + v);
    }

}
