package com.example.proyectopractica.employees;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable String id){
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping("/new")
    public ResponseEntity<EmployeeDto> addEmployee(@Valid @RequestBody EmployeeDto request){
        EmployeeDto dto = service.addEmployee(request);
        return ResponseEntity.created(URI.create("/api/employees/"+dto.id())).body(dto);
    }
}
