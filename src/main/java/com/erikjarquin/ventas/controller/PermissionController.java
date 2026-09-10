package com.erikjarquin.ventas.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erikjarquin.ventas.model.dto.Permissions.PermissionResponse;
import com.erikjarquin.ventas.service.PermissionService;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService){
        this.permissionService=permissionService;
    }

    //Ver todos los permisos
    @PreAuthorize("hasAuthority('VER_ROLES')")
    @GetMapping
    public List<PermissionResponse> getAllPermissions(){
        return permissionService.getAllPermissions();
    }
}
