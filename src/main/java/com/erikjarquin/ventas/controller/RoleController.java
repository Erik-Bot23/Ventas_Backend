package com.erikjarquin.ventas.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erikjarquin.ventas.model.dto.RoleDto;
import com.erikjarquin.ventas.service.RoleService;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "http://localhost:4200")
public class RoleController {
    private final RoleService service;

    public RoleController(RoleService service){
        this.service=service;
    }

    @GetMapping
    public List<RoleDto> getAllRoles(){
        return service.getAllRoles();
    }

    @PostMapping
    public RoleDto save(@RequestBody RoleDto dto){
        return service.save(dto);
    }

    @DeleteMapping
    public void delete(@PathVariable Long id){
        service.delete(id);
    }
}
