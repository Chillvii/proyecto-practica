package com.example.proyectopractica.employees;

import com.example.proyectopractica.departments.DepartmentDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Representación pública del empleado expuesta por la API.
 *
 * Se mantiene separada de la entidad {@link Employee} a propósito: si mañana la
 * entidad gana campos internos (auditoría, soft-delete, etc.) no se filtran a la API.
 */
public record EmployeeDto(
        String id,
        @NotBlank(message = "El nombre no puede estar vacio")
        String firstName,
        @NotBlank(message = "El apellido no puede estar vacio")
        String lastName,
        @Email
        @NotBlank(message = "El email no puede estar vacio")
        String email,
        @NotBlank(message = "El puesto no puede estar vacio")
        String position,
        LocalDate hiredAt,
        String departmentName
) {
}
