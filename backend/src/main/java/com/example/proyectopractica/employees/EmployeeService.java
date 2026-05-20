package com.example.proyectopractica.employees;

import java.time.LocalDate;
import java.util.List;

import com.example.proyectopractica.common.EmployeeNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public PageResponse getEmployees(String firstName,
                                     String position, LocalDate from,
                                     LocalDate to, Pageable pageable) {
        Page<Employee> page;

        boolean hasFirstName = firstName != null && !firstName.isBlank();
        boolean hasPosition  = position != null && !position.isBlank();
        boolean hasRange     = from != null && to != null;

        if (hasFirstName && hasPosition && hasRange)
            page = repository.findByFirstNameAndPositionAndHiredAtRange(firstName, position, from, to, pageable);
        else if (hasFirstName && hasPosition)
            page = repository.findByFirstNameIgnoreCaseAndPositionIgnoreCase(firstName, position, pageable);
        else if (hasFirstName && hasRange)
            page = repository.findByFirstNameAndHiredAtRange(firstName, from, to, pageable);
        else if (hasPosition && hasRange)
            page = repository.findByPositionAndHiredAtRange(position, from, to, pageable);
        else if (hasFirstName)
            page = repository.findByFirstNameIgnoreCase(firstName, pageable);
        else if (hasPosition)
            page = repository.findByPositionIgnoreCase(position, pageable);
        else if (hasRange)
            page = repository.findByHiredAtRange(from, to, pageable);
        else
            page = repository.findAll(pageable);

        return new PageResponse(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
