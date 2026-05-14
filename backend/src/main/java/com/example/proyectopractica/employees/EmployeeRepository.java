package com.example.proyectopractica.employees;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Boolean existsByEmail(String email);
}
