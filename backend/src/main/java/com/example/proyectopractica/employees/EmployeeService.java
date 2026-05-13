package com.example.proyectopractica.employees;

import java.util.List;

import com.example.proyectopractica.common.EmployeeNotFoundException;
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

    public EmployeeDto findById(String id){
        Employee employee = repository.findById(id)
                .orElseThrow(()-> new EmployeeNotFoundException(id));
        return EmployeeMapper.toDto(employee);
    }
}
