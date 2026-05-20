package com.example.proyectopractica.common;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Manejador global de excepciones.
 *
 * Centraliza el formato de respuesta de errores ({@link ApiError}) para que la API
 * exponga siempre el mismo contrato pase lo que pase. Cuando añadas nuevos endpoints
 * y se generen excepciones de negocio propias, conviene mapearlas aquí (por ejemplo
 * crear EmployeeNotFoundException → 404 dedicado).
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Recurso no encontrado", request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos", request, fieldErrors);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateKeyException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "Recurso duplicado: " + rootCause(ex), request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest request) {
        log.error("Excepción no controlada", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request, null);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiError> handleEmployeeNotFound(EmployeeNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND,"Empleado con id "+ ex.getMessage() + " no encontrado", request,
                null);
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<ApiError> handleDepartmentNotFound(DepartmentNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND,"Empleado con id "+ ex.getMessage() + " no encontrado", request,
                null);
    }

    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<ApiError> handleResourceInUse(ResourceInUseException ex, HttpServletRequest request){
        return build(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest request,
                                           Map<String, String> fieldErrors) {
        ApiError body = ApiError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.status(status).body(body);
    }

    private static String rootCause(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        return cur.getMessage();
    }
}
