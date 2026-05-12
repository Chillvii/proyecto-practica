package com.example.proyectopractica.employees;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repository;

    public List<EmployeeDto> findAll() {
        return repository.findAll()
                .stream()
                .map(EmployeeMapper::toDto)
                .toList();
    }
}
