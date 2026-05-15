package com.example.proyectopractica.employees;

import java.util.List;

import com.example.proyectopractica.common.EmployeeNotFoundException;
import org.springframework.dao.DuplicateKeyException;
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

    public EmployeeDto addEmployee(EmployeeDto request){
        if (repository.existsByEmail(request.email())){
            throw new DuplicateKeyException(request.email());
        }
        return EmployeeMapper.toDto(repository.save(EmployeeMapper.toEntity(request)));
    }

    public EmployeeDto updateEmployee(String id,EmployeeDto request){
        Employee employee = repository.findById(id)
                .orElseThrow(()-> new EmployeeNotFoundException(id));

        if (repository.existsByEmail(request.email())
                && !employee.getEmail().equals(request.email())) {
            throw new DuplicateKeyException(request.email());
        }

        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setEmail(request.email());
        employee.setPosition(request.position());
        //employee.setHiredAt(request.hiredAt());

        return EmployeeMapper.toDto(repository.save(employee));
    }

    public void deleteEmployee(String id){
        Employee employee = repository.findById(id)
                .orElseThrow(()-> new EmployeeNotFoundException(id));
        repository.delete(employee);
    }
}
