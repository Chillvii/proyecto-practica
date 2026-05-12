package com.example.proyectopractica.employees;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

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
}
