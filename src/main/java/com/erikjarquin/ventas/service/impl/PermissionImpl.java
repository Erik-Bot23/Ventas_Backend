package com.erikjarquin.ventas.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erikjarquin.ventas.mapper.PermissionMapper;
import com.erikjarquin.ventas.model.dto.Permissions.PermissionResponse;
import com.erikjarquin.ventas.repository.PermissionRepository;
import com.erikjarquin.ventas.service.PermissionService;

@Service
@Transactional(readOnly = true) //
public class PermissionImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    //Constructor
    public PermissionImpl(PermissionRepository permissionRepository){
        this.permissionRepository=permissionRepository;
    }

    //Ver todos los permisos
    @Override
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                                    .map(PermissionMapper::toDto).toList();
    }
}
