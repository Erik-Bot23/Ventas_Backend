package com.erikjarquin.ventas.mapper;

import com.erikjarquin.ventas.model.dto.RoleDto;
import com.erikjarquin.ventas.model.entity.RoleEntity;

public class RoleMapper {
    public static RoleDto toDto(RoleEntity entity){
        RoleDto dto = new RoleDto();

        dto.setId(entity.getId());
        dto.setRole(entity.getName());

        return dto;
    }
}
