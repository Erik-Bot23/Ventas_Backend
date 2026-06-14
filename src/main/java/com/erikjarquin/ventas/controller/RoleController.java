package com.erikjarquin.ventas.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erikjarquin.ventas.model.dto.RoleDto;
import com.erikjarquin.ventas.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService service;

    public RoleController(RoleService service){
        this.service=service;
    }

    @GetMapping
    public List<RoleDto> getRoles(){
        return service.getAllRoles();
    }
}
