package com.example.proyectopractica.departments;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DepartmentRepository extends MongoRepository<Department, String> {
    Boolean existsByName(String name);
    Optional<Department> findByName(String name);
}
