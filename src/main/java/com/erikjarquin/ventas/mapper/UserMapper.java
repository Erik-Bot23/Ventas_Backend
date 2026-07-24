package com.erikjarquin.ventas.mapper;

import com.erikjarquin.ventas.model.dto.UserRole.UserDto;
import com.erikjarquin.ventas.model.entity.UserEntity;

public class UserMapper {
    public static UserDto toDto(UserEntity entity){
        if(entity == null) return null;

        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setActive(entity.isActive());

        if (entity.getRole() != null) {
            dto.setRoleId(entity.getRole().getId());
            dto.setRoleName(entity.getRole().getName());
        }
        
        return dto;
    }
}
