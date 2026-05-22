package com.example.proyectopractica.employees;

import java.time.LocalDate;

import com.example.proyectopractica.departments.Department;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Documento de empleado persistido en MongoDB.
 *
 * Cuando añadas más entidades, copia esta plantilla: anota la clase con @Document,
 * usa @Id en el identificador y @Indexed(unique = true) en los campos que deban ser únicos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employees")
public class Employee {

    @Id
    private String id;

    private String firstName;

    private String lastName;

    @Indexed(unique = true)
    private String email;

    private String position;

    private LocalDate hiredAt;

    // Relación embed: guardamos el nombre del departamento directamente en el empleado.
    // Motivo: evita joins, simplifica las queries y es suficiente para esta aplicacion.
    // Contras: si se renombra un departamento hay que propagar el cambio
    // (ver DepartmentService.update → employeeRepository.updateDepartmentName).
    private String departmentName;
}
