package com.example.proyectopractica.employees;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import com.example.proyectopractica.common.EmployeeNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    }

    @Test
    void getEmployee_devuelve404() throws Exception{
        when(service.findById("11")).thenThrow(new EmployeeNotFoundException("Empleado no encontrado"));

        mockMvc.perform(get("/api/employees/11"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Empleado no encontrado"));
    }
}
