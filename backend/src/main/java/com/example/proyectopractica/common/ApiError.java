package com.example.proyectopractica.common;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

/**
 * Contrato uniforme de error que devuelve la API.
 *
 * Lo construye {@link GlobalExceptionHandler} para todas las respuestas 4xx/5xx.
 * Cuando el error es de validación, se rellena {@code fieldErrors}; en otros casos
 * va omitido para no añadir ruido a la respuesta.
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
}
