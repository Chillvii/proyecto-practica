package com.example.proyectopractica.employees;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import com.example.proyectopractica.common.EmployeeNotFoundException;
import com.example.proyectopractica.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.cors.CorsConfigurationSource;

@Slf4j
@WebMvcTest(controllers = EmployeeController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        })
@WithMockUser
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService service;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void list_devuelve200ConArrayDeEmpleados() throws Exception {
        when(service.findAll()).thenReturn(List.of(
                new EmployeeDto("1", "Ana", "García", "ana@example.com",
                        "Backend Developer", LocalDate.of(2022, 3, 14),null),
                new EmployeeDto("2", "Bruno", "López", "bruno@example.com",
                        "Frontend Developer", LocalDate.of(2021, 7, 1),null)
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
    void getEmployee_devuelve200() throws Exception {
        when(service.findById("1")).thenReturn(
                new EmployeeDto("1", "Ana", "García", "ana@example.com",
                        "Backend Developer", LocalDate.of(2022, 3, 14),null)
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
    void getEmployee_devuelve404() throws Exception {
        when(service.findById("11")).thenThrow(new EmployeeNotFoundException("11"));

        mockMvc.perform(get("/api/employees/11"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Empleado con id 11 no encontrado"));
        //.andDo(print());
    }

    @Test
    void addEmployee_devuelve201() throws Exception {
        EmployeeDto employee = new EmployeeDto("20", "Antonio", "Banderas"
                , "antonio_banderas@example.com", "Backend Developer",
                LocalDate.of(2025, 2, 25),null);

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
                .andExpect(jsonPath("$.id").value("20"))
                .andExpect(jsonPath("$.firstName").value("Antonio"))
                .andExpect(jsonPath("$.lastName").value("Banderas"))
                .andExpect(jsonPath("$.email").value("antonio_banderas@example.com"))
                .andExpect(jsonPath("$.position").value("Backend Developer"));
        //.andDo(print());
    }

    @Test
    void addEmployee_devuelve409() throws Exception {
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

    @Test
    void addEmployee_devuelve400_cuandoBodyInvalido() throws Exception {
        mockMvc.perform(post("/api/employees/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "firstName": "",
                              "lastName": "X",
                              "email": "no-es-un-email",
                              "position": "Dev"
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
                //.andDo(print());
    }

    @Test
    void updateEmployee_devuelve200() throws Exception {
        EmployeeDto updated = new EmployeeDto(
                "20",
                "Antonio",
                "Banderas",
                "antonio@example.com",
                "Frontend Developer",
                LocalDate.of(2025, 2, 25),
                null
        );

        when(service.updateEmployee(any(), any())).thenReturn(updated);

        mockMvc.perform(put("/api/employees/20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "firstName": "Antonio",
                                      "lastName": "Banderas",
                                      "email": "antonio@example.com",
                                      "position": "Frontend Developer"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Antonio"))
                .andExpect(jsonPath("$.lastName").value("Banderas"))
                .andExpect(jsonPath("$.email").value("antonio@example.com"))
                .andExpect(jsonPath("$.position").value("Frontend Developer"));
    }

    @Test
    void updateEmployee_devuelve409() throws Exception{
        when(service.updateEmployee(any(), any()))
                .thenThrow(new DuplicateKeyException("Email duplicado"));

        mockMvc.perform(put("/api/employees/20")
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
                .andExpect(jsonPath("$.message")
                        .value("Recurso duplicado: Email duplicado"));
    }

    @Test
    void updateEmployee_devuelve404() throws Exception {
        when(service.updateEmployee(any(), any()))
                .thenThrow(new EmployeeNotFoundException("20"));

        mockMvc.perform(put("/api/employees/20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "firstName": "Antonio",
                                      "lastName": "Banderas",
                                      "email": "antonio@example.com",
                                      "position": "Backend Developer"
                                    }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Empleado con id 20 no encontrado"));
    }

    @Test
    void deleteEmployee_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/employees/20"))
                .andExpect(status().isNoContent());

        verify(service).deleteEmployee("20");
    }

    @Test
    void deleteEmployee_devuelve404() throws Exception {
        doThrow(new EmployeeNotFoundException("20"))
                .when(service).deleteEmployee("20");

        mockMvc.perform(delete("/api/employees/20"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Empleado con id 20 no encontrado"));

    }
}
