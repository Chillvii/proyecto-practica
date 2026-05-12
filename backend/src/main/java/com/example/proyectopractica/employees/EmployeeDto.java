package com.example.proyectopractica.employees;

import java.time.LocalDate;

/**
 * Representación pública del empleado expuesta por la API.
 *
 * Se mantiene separada de la entidad {@link Employee} a propósito: si mañana la
 * entidad gana campos internos (auditoría, soft-delete, etc.) no se filtran a la API.
 */
public record EmployeeDto(
        String id,
        String firstName,
        String lastName,
        String email,
        String position,
        LocalDate hiredAt
) {
}
