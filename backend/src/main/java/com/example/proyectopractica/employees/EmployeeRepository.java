package com.example.proyectopractica.employees;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Boolean existsByEmail(String email);

    // sin fechas — métodos derivados son suficientes
    @Query("{ 'firstName': { $regex: ?0, $options: 'i' } }")
    Page<Employee> findByFirstNameIgnoreCase(String firstName, Pageable pageable);

    @Query("{ 'position': { $regex: ?0, $options: 'i' } }")
    Page<Employee> findByPositionIgnoreCase(String position, Pageable pageable);

    @Query("{ 'firstName': { $regex: ?0, $options: 'i' }, 'position': { $regex: ?1, $options: 'i' } }")
    Page<Employee> findByFirstNameIgnoreCaseAndPositionIgnoreCase(
            String firstName, String position, Pageable pageable);

    // con fechas — @Query para garantizar $gte y $lte inclusivos
    @Query("{ 'hiredAt': { $gte: ?0, $lte: ?1 } }")
    Page<Employee> findByHiredAtRange(LocalDate from, LocalDate to, Pageable pageable);

    @Query("{ 'firstName': { $regex: ?0, $options: 'i' }, 'hiredAt': { $gte: ?1, $lte: ?2 } }")
    Page<Employee> findByFirstNameAndHiredAtRange(String firstName, LocalDate from, LocalDate to, Pageable pageable);

    @Query("{ 'position': { $regex: ?0, $options: 'i' }, 'hiredAt': { $gte: ?1, $lte: ?2 } }")
    Page<Employee> findByPositionAndHiredAtRange(String position, LocalDate from, LocalDate to, Pageable pageable);

    @Query("{ 'firstName': { $regex: ?0, $options: 'i' }, 'position': { $regex: ?1, $options: 'i' }, 'hiredAt': { $gte: ?2, $lte: ?3 } }")
    Page<Employee> findByFirstNameAndPositionAndHiredAtRange(
            String firstName, String position, LocalDate from, LocalDate to, Pageable pageable);
}
