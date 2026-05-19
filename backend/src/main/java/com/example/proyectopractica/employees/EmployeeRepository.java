package com.example.proyectopractica.employees;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Boolean existsByEmail(String email);

    Page<Employee> findByFirstNameAndPositionIgnoreCase(String firstName, String position, Pageable pageable);
    Page<Employee> findByFirstNameIgnoreCase(String firstName, Pageable pageable);
    Page<Employee> findByPositionIgnoreCase(String position, Pageable pageable);

    @Query("SELECT e FROM Employee e " +
            "WHERE (:firstName IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) " +
            "AND (:position IS NULL OR LOWER(e.position) = LOWER(:position)) " +
            "AND (:hiredAfter IS NULL OR e.hiredAt >= :hiredAfter) " +
            "AND (:hiredBefore IS NULL OR e.hiredAt <= :hiredBefore)")
    Page<Employee> search(
            @Param("firstName") String firstName,
            @Param("position") String position,
            @Param("hiredAfter") LocalDate hiredAfter,
            @Param("hiredBefore") LocalDate hiredBefore,
            Pageable pageable);
}
