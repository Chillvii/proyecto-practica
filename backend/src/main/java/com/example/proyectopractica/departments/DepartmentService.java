package com.example.proyectopractica.departments;

import com.example.proyectopractica.common.DepartmentNotFoundException;
import com.example.proyectopractica.common.ResourceInUseException;
import com.example.proyectopractica.employees.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository repository;

    private final DepartmentMapper mapper;

    public List<DepartmentDto> getAll() {
        return repository.findAll().stream()
                .map(DepartmentMapper::toDto)
                .toList();
    }

    public DepartmentDto create(DepartmentDto request) {
        if (repository.existsByName(request.name()))
            throw new DuplicateKeyException(request.name());

        Department saved = repository.save(DepartmentMapper.toEntity(request));

        return DepartmentMapper.toDto(saved);
    }

    public DepartmentDto update(String id, String newName) {
        Department dept = repository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));

        if (repository.existsByName(newName))
            throw new DuplicateKeyException(newName);

        String oldName = dept.getName();
        dept.setName(newName);
        repository.save(dept);
        employeeRepository.updateDepartmentName(oldName, newName);

        return DepartmentMapper.toDto(dept);
    }

    public void delete(String id) {
        Department dept = repository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));

        // 409 si hay empleados en ese departamento
        if (employeeRepository.existsByDepartmentName(dept.getName()))
            throw new ResourceInUseException("Departamento con empleados asignados");

        repository.delete(dept);
    }
}
