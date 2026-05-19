package com.example.proyectopractica.employees;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageResponse {
    List<EmployeeDto> content;
    int page;
    int size;
    long totalElements;
    int totalPages;
}
