package com.example.proyectopractica.employees;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Arrays;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
}
