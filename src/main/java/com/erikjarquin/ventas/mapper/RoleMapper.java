package com.erikjarquin.ventas.mapper;

import com.erikjarquin.ventas.model.dto.UserRole.RoleDto;
import com.erikjarquin.ventas.model.entity.RoleEntity;

public class RoleMapper {
    public static RoleDto toDto(RoleEntity entity){
        if(entity == null){
            return null;
        }

        RoleDto dto = new RoleDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());

        return dto;
    }

    public static RoleEntity toEntity(RoleDto dto){
        if(dto == null){
            return null;
        }

        RoleEntity entity = new RoleEntity();

        entity.setId(dto.getId());
        entity.setName(dto.getName());

        return entity;
    }
}
