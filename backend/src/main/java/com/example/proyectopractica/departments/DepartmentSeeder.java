package com.example.proyectopractica.departments;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Inserta departamentos de ejemplo al primer arranque (sólo si la colección está vacía).
 *
 * Debe ejecutarse antes que EmployeeSeeder para que los empleados puedan
 * referenciar departamentos existentes. Spring garantiza el orden si usas
 * @Order o si los seeders son independientes (este no depende de employees).
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class DepartmentSeeder implements CommandLineRunner {

    private final DepartmentRepository repository;

    @Override
    public void run(String... args) {
        long existing = repository.count();
        if (existing > 0) {
            log.info("Seed omitido: la colección 'departments' ya contiene {} documentos.", existing);
            return;
        }

        List<Department> sample = List.of(
                build("Engineering"),
                build("Product"),
                build("Design"),
                build("DevOps"),
                build("QA"),
                build("Data"),
                build("Human Resources"),
                build("Finance")
        );

        repository.saveAll(sample);
        log.info("Seed completado: insertados {} departamentos de ejemplo.", sample.size());
    }

    private static Department build(String name) {
        return Department.builder()
                .name(name)
                .build();
    }
}