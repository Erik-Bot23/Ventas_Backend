package com.erikjarquin.ventas.service;

import java.util.List;

import com.erikjarquin.ventas.model.dto.RoleDto;

public interface  RoleService {
    List<RoleDto> getAllRoles();
    RoleDto save(RoleDto dto);
    void delete(Long id);
}
