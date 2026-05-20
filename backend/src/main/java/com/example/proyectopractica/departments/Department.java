package com.example.proyectopractica.departments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "departments")
public class Department {
    @Id
    String id;
    @Indexed(unique = true)
    String name;

    public Department(String name){
        this.name = name;
    }
}
