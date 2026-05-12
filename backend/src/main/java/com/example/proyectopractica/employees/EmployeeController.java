package com.example.proyectopractica.employees;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * API pública del recurso Employee.
 *
 * De momento sólo se expone el listado. Tu siguiente reto: añadir GET /{id}, POST,
 * PUT /{id} y DELETE /{id}, validando las entradas con Bean Validation y mapeando
 * los errores en {@link com.example.proyectopractica.common.GlobalExceptionHandler}.
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @GetMapping
    public List<EmployeeDto> list() {
        return service.findAll();
    }
}
