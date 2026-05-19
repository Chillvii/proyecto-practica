package com.example.proyectopractica.employees;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable String id, @Valid @RequestBody EmployeeDto request){
        return ResponseEntity.ok(service.updateEmployee(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id){
        service.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    //filtros por nombre y posicion
    @GetMapping
    public ResponseEntity<Page<EmployeeDto>> getEmployees(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) LocalDate hiredAfter,
            @RequestParam(required = false) LocalDate hiredBefore,
            Pageable pageable){

        return ResponseEntity.ok(service.getEmployees(firstName,position, hiredAfter, hiredBefore, pageable));
    }
}
