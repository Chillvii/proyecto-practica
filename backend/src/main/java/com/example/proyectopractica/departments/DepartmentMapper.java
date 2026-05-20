package com.example.proyectopractica.departments;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class DepartmentMapper {
    public static DepartmentDto toDto(Department entity) {
        if (entity == null) {
            return null;
        }
        return new DepartmentDto(
                entity.getId(),
                entity.getName()
        );
    }

    public static Department toEntity(DepartmentDto dto) {
        if (dto == null) {
            return null;
        }

        return Department.builder()
                .id(dto.id())
                .name(dto.name())
                .build();
    }
}
