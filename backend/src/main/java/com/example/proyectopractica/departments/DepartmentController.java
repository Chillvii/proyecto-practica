package com.example.proyectopractica.departments;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService service;

    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<DepartmentDto> create(@RequestBody DepartmentDto request) {
        DepartmentDto dto = service.create(request);
        return ResponseEntity.created(URI.create("/api/departments/"+request.id())).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDto> update(@PathVariable String id,
                                                @RequestBody String newName) {
        return ResponseEntity.ok(service.update(id, newName));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
