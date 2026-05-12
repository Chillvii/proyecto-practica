package com.example.proyectopractica.employees;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

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
}
