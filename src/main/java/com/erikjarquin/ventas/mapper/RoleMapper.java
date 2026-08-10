package com.erikjarquin.ventas.mapper;

import java.util.List;

import com.erikjarquin.ventas.model.dto.Permissions.PermissionResponse;
import com.erikjarquin.ventas.model.dto.Role.RoleDto;
import com.erikjarquin.ventas.model.entity.RoleEntity;

public class RoleMapper {
    public static RoleDto toDto(RoleEntity entity){
        if(entity == null){
            return null;
        }

        List<PermissionResponse> permissions = entity.getPermissions()
                                                .stream().map(PermissionMapper::toDto).toList();

        return new RoleDto(
            entity.getId(),
            entity.getName(),
            permissions);
    }
}
