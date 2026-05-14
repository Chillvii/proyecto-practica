package com.example.proyectopractica.employees;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import com.example.proyectopractica.common.EmployeeNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@Slf4j
@WebMvcTest(controllers = EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService service;

    @Test
    void list_devuelve200ConArrayDeEmpleados() throws Exception {
        when(service.findAll()).thenReturn(List.of(
                new EmployeeDto("1", "Ana", "García", "ana@example.com",
                        "Backend Developer", LocalDate.of(2022, 3, 14)),
                new EmployeeDto("2", "Bruno", "López", "bruno@example.com",
                        "Frontend Developer", LocalDate.of(2021, 7, 1))
        ));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].firstName").value("Ana"))
                .andExpect(jsonPath("$[0].email").value("ana@example.com"))
                .andExpect(jsonPath("$[1].position").value("Frontend Developer"));
    }

    @Test
    void getEmployee_devuelve200() throws Exception{
        when(service.findById("1")).thenReturn(
                new EmployeeDto("1", "Ana", "García", "ana@example.com",
                        "Backend Developer", LocalDate.of(2022, 3, 14))
        );

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.firstName").value("Ana"))
                .andExpect(jsonPath("$.email").value("ana@example.com"))
                .andExpect(jsonPath("$.position").value("Backend Developer"));
                //.andDo(print());
    }

    @Test
    void getEmployee_devuelve404() throws Exception{
        when(service.findById("11")).thenThrow(new EmployeeNotFoundException("Empleado no encontrado"));

        mockMvc.perform(get("/api/employees/11"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Empleado no encontrado"));
                //.andDo(print());
    }

    @Test
    void addEmployee_devuelve201() throws Exception {
        EmployeeDto employee = new EmployeeDto("20", "Antonio", "Banderas"
                , "antonio_banderas@example.com", "Backend Developer",
                LocalDate.of(2025, 2, 25));

        when(service.addEmployee(any())).thenReturn(employee);

        mockMvc.perform(post("/api/employees/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "firstName": "Antonio",
                                      "lastName": "Banderas",
                                      "email": "antonio_banderas@example.com",
                                      "position": "Backend Developer"
                                    }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/employees/20"))
                .andExpect(jsonPath("$.firstName").value("Antonio"))
                .andExpect(jsonPath("$.lastName").value("Banderas"))
                .andExpect(jsonPath("$.email").value("antonio_banderas@example.com"))
                .andExpect(jsonPath("$.position").value("Backend Developer"));
                //.andDo(print());
    }

    @Test
    void addEmployee_devuelve409() throws Exception {
        EmployeeDto employee1= new EmployeeDto("1", "Ana", "García", "ana@example.com",
                "Backend Developer", LocalDate.of(2022, 3, 14));

        EmployeeDto employee2 = new EmployeeDto("20", "Antonio", "Banderas"
                , "ana@example.com", "Backend Developer", LocalDate.of(2025, 2, 25));

        when(service.addEmployee(any())).thenThrow(new DuplicateKeyException("Email duplicado"));

        mockMvc.perform(post("/api/employees/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "firstName": "Antonio",
                                      "lastName": "Banderas",
                                      "email": "ana@example.com",
                                      "position": "Backend Developer"
                                    }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Recurso duplicado: Email duplicado"));
                //.andDo(print());
    }
}
