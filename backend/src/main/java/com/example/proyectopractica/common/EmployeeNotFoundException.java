package com.example.proyectopractica.common;

import lombok.Getter;

@Getter
public class EmployeeNotFoundException extends RuntimeException {

    private final String id;

    public EmployeeNotFoundException(String id) {
        this.id = id;
    }

}
