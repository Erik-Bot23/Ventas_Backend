package com.erikjarquin.ventas.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erikjarquin.ventas.model.dto.Role.CreateRoleRequest;
import com.erikjarquin.ventas.model.dto.Role.RoleDto;
import com.erikjarquin.ventas.model.dto.Role.UpdateRoleRequest;
import com.erikjarquin.ventas.service.RoleService;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "http://localhost:4200")
public class RoleController {
    private final RoleService service;

    public RoleController(RoleService service){
        this.service=service;
    }

    @PreAuthorize("hasAuthority('VER_ROLES')")
    @GetMapping
    public List<RoleDto> getAllRoles(){
        return service.getAllRoles();
    }

    @PreAuthorize("hasAuthority('VER_ROLES')")
    @GetMapping("/{id}")
    public RoleDto getRoleById(@PathVariable Long id){
        return service.getRoleById(id);
    }

    @PreAuthorize("hasAuthority('CREAR_ROLE')")
    @PostMapping
    public RoleDto createRole(@RequestBody CreateRoleRequest request){
        return service.createRole(request);
    }

    @PreAuthorize("hasAuthority('EDITAR_ROLE')")
    @PostMapping("/{id}")
    public RoleDto updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest request){
        return service.updateRole(id, request);
    }

    @PreAuthorize("hasAuthority('ELIMINAR_ROLE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id){
        service.deleteRole(id);
        
        return ResponseEntity.noContent().build();
    }
}
