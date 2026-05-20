package com.example.proyectopractica.employees;

/**
 * Mapeo manual entre {@link Employee} (entidad Mongo) y {@link EmployeeDto} (API).
 *
 * Mantén el mapeo aquí en lugar de mezclarlo con el service: cuando el modelo crezca
 * será más fácil añadir un MapStruct si hace falta. Se incluye {@link #toEntity}
 * aunque ahora no se usa, para que tengas la plantilla cuando implementes POST/PUT.
 */
public final class EmployeeMapper {

    private EmployeeMapper() {
    }

    public static EmployeeDto toDto(Employee entity) {
        if (entity == null) {
            return null;
        }
        return new EmployeeDto(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPosition(),
                entity.getHiredAt(),
                entity.getDepartmentName()
        );
    }

    public static Employee toEntity(EmployeeDto dto) {
        if (dto == null) {
            return null;
        }
        return Employee.builder()
                .id(dto.id())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .position(dto.position())
                .hiredAt(dto.hiredAt())
                .departmentName(dto.departmentName())
                .build();
    }
}
