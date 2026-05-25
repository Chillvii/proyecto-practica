package com.example.proyectopractica.employees;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.proyectopractica.common.EmployeeNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @InjectMocks
    private EmployeeService service;

    @Test
    void findAll_devuelveLosEmpleadosMapeadosADto() {
        Employee ana = Employee.builder()
                .id("1")
                .firstName("Ana")
                .lastName("García")
                .email("ana@example.com")
                .position("Backend Developer")
                .hiredAt(LocalDate.of(2022, 3, 14))
                .build();

        Employee bruno = Employee.builder()
                .id("2")
                .firstName("Bruno")
                .lastName("López")
                .email("bruno@example.com")
                .position("Frontend Developer")
                .hiredAt(LocalDate.of(2021, 7, 1))
                .build();

        when(repository.findAll()).thenReturn(List.of(ana, bruno));

        List<EmployeeDto> result = service.findAll();

        assertThat(result)
                .hasSize(2)
                .extracting(EmployeeDto::email)
                .containsExactly("ana@example.com", "bruno@example.com");

        assertThat(result.get(0))
                .returns("1", EmployeeDto::id)
                .returns("Ana", EmployeeDto::firstName)
                .returns(LocalDate.of(2022, 3, 14), EmployeeDto::hiredAt);
    }

    @Test
    void findById_devuelveEmpleadoDto() {

        Employee ana = Employee.builder()
                .id("1")
                .firstName("Ana")
                .lastName("García")
                .email("ana@example.com")
                .position("Backend Developer")
                .hiredAt(LocalDate.of(2022, 3, 14))
                .build();

        when(repository.findById("1")).thenReturn(Optional.of(ana));

        EmployeeDto result = service.findById("1");

        assertThat(result)
                .returns("1", EmployeeDto::id)
                .returns("Ana", EmployeeDto::firstName)
                .returns(LocalDate.of(2022, 3, 14), EmployeeDto::hiredAt);

    }

    @Test
    void findById_404_EmpleadoNoEncontrado() {
        when(repository.findById("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("1"))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void addEmployee_201_EmpleadoCreado() {

        Employee entity = new Employee(
                "20",
                "Antonio",
                "Banderas",
                "antonio_banderas@example.com",
                "Backend Developer",
                LocalDate.of(2025, 2, 25),
                null
        );

        EmployeeDto request = new EmployeeDto(
                "20",
                "Antonio",
                "Banderas",
                "antonio_banderas@example.com",
                "Backend Developer",
                LocalDate.of(2025, 2, 25),
                null
        );

        when(repository.save(any())).thenReturn(entity);

        EmployeeDto result = service.addEmployee(request);

        assertThat(result)
                .returns("Antonio", EmployeeDto::firstName)
                .returns("Banderas", EmployeeDto::lastName)
                .returns("antonio_banderas@example.com", EmployeeDto::email)
                .returns(LocalDate.of(2025, 2, 25), EmployeeDto::hiredAt);
    }

    @Test
    void addEmployee_409_EmpleadoCreado() {

        EmployeeDto request = new EmployeeDto(
                "20",
                "Antonio",
                "Banderas",
                "ana@example.com",
                "Backend Developer",
                LocalDate.of(2025, 2, 25),
                null
        );

        when(repository.existsByEmail("ana@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.addEmployee(request))
                .isInstanceOf(DuplicateKeyException.class);

    }

    @Test
    void updateEmployee_200_OK() {

        Employee existing = Employee.builder()
                .id("20")
                .firstName("Antonio")
                .lastName("Banderas")
                .email("antonio@example.com")
                .position("Backend Developer")
                .hiredAt(LocalDate.of(2022, 3, 14))
                .build();

        EmployeeDto request = new EmployeeDto(
                "20",
                "Antonio",
                "Banderas",
                "antonio@example.com",
                "Frontend Developer",
                LocalDate.of(2025, 2, 25),
                null
        );

        when(repository.findById("20")).thenReturn(Optional.of(existing));
        when(repository.existsByEmail("antonio@example.com")).thenReturn(false);
        when(repository.save(any())).thenReturn(existing);

        EmployeeDto result = service.updateEmployee("20", request);

        assertThat(result)
                .returns("Antonio", EmployeeDto::firstName)
                .returns("Banderas", EmployeeDto::lastName);
    }

    @Test
    void updateEmployee_404_NoExiste() {

        EmployeeDto request = new EmployeeDto(
                "20",
                "Antonio",
                "Banderas",
                "antonio@example.com",
                "Frontend Developer",
                LocalDate.of(2025, 2, 25),
                null
        );

        when(repository.findById("20")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateEmployee("20", request))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void updateEmployee_409_EmailDuplicado() {

        Employee existing = Employee.builder()
                .id("20")
                .email("antonio@example.com")
                .build();

        EmployeeDto request = new EmployeeDto(
                "20",
                "Antonio",
                "Banderas",
                "ana@example.com",
                "Backend Developer",
                LocalDate.of(2025, 2, 25),
                null
        );

        when(repository.findById("20")).thenReturn(Optional.of(existing));
        when(repository.existsByEmail("ana@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.updateEmployee("20", request))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void deleteEmployee_204_OK() {

        Employee existing = Employee.builder()
                .id("20")
                .build();

        when(repository.findById("20")).thenReturn(Optional.of(existing));
        doNothing().when(repository).delete(existing);

        service.deleteEmployee("20");
    }

    @Test
    void deleteEmployee_404_NoExiste() {

        when(repository.findById("20")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteEmployee("20"))
                .isInstanceOf(EmployeeNotFoundException.class);
    }
}
