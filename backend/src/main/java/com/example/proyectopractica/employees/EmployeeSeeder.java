package com.example.proyectopractica.employees;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Inserta 10 empleados de ejemplo al primer arranque (sólo si la colección está vacía).
 *
 * Los datos son deterministas a propósito: arrancar la app dos veces sobre una base
 * limpia produce siempre los mismos empleados, lo cual es útil para depurar.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(2)
public class EmployeeSeeder implements CommandLineRunner {

    private final EmployeeRepository repository;

    @Override
    public void run(String... args) {
        long existing = repository.count();
        if (existing > 0) {
            log.info("Seed omitido: la colección 'employees' ya contiene {} documentos.", existing);
            return;
        }

        List<Employee> sample = List.of(
                build("Ana",      "García",    "ana.garcia@example.com",      "Backend Developer",   LocalDate.of(2022, 3, 14),  "Engineering"),
                build("Bruno",    "López",     "bruno.lopez@example.com",     "Frontend Developer",  LocalDate.of(2021, 7,  1),  "Engineering"),
                build("Carmen",   "Martínez",  "carmen.martinez@example.com", "Tech Lead",           LocalDate.of(2019, 11, 23), "Engineering"),
                build("David",    "Rodríguez", "david.rodriguez@example.com", "DevOps Engineer",     LocalDate.of(2020, 2, 10),  "DevOps"),
                build("Elena",    "Sánchez",   "elena.sanchez@example.com",   "QA Engineer",         LocalDate.of(2023, 5,  5),  "QA"),
                build("Fernando", "Pérez",     "fernando.perez@example.com",  "Product Manager",     LocalDate.of(2018, 9, 17),  "Product"),
                build("Gloria",   "Ruiz",      "gloria.ruiz@example.com",     "UX Designer",         LocalDate.of(2022, 1, 30),  "Design"),
                build("Héctor",   "Jiménez",   "hector.jimenez@example.com",  "Backend Developer",   LocalDate.of(2023, 8, 12),  "Engineering"),
                build("Irene",    "Moreno",    "irene.moreno@example.com",    "Data Engineer",       LocalDate.of(2021, 4, 19),  "Data"),
                build("Javier",   "Torres",    "javier.torres@example.com",   "Fullstack Developer", LocalDate.of(2024, 1,  8),  "Engineering")
        );

        repository.saveAll(sample);
        log.info("Seed completado: insertados {} empleados de ejemplo.", sample.size());
    }

    private static Employee build(String firstName, String lastName, String email, String position, LocalDate hiredAt, String departmentName) {
        return Employee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .position(position)
                .hiredAt(hiredAt)
                .departmentName(departmentName)
                .build();
    }
}
