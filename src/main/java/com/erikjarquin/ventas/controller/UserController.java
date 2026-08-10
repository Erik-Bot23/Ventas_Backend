package com.erikjarquin.ventas.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erikjarquin.ventas.model.dto.User.CreateUserRequest;
import com.erikjarquin.ventas.model.dto.User.UpdateUserRequest;
import com.erikjarquin.ventas.model.dto.User.UserDto;
import com.erikjarquin.ventas.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service){
        this.service = service;
    }

    @PreAuthorize("hasAuthority('VER_USUARIOS')")
    @GetMapping
    public List<UserDto> getAllUsers(){
        return service.getAllUsers();
    }

    @PreAuthorize("hasAuthority('CREAR_USUARIO')")
    @PostMapping
    public UserDto createUser(@RequestBody CreateUserRequest request){
        return service.createUser(request);
    }

    @PreAuthorize("hasAuthority('EDITAR_USUARIO')")
    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request){
        return service.updateUser(id, request);
    }

    @PreAuthorize("hasAuthority('DESACTIVAR_USUARIO')")
    @DeleteMapping("/{id}")
    public void deactivateUser(@PathVariable Long id){
        service.deactivateUser(id);
    }

    @PreAuthorize("hasAuthority('ACTIVAR_USUARIO')")
    @PatchMapping("/{id}/active")
    public void activateUser(@PathVariable Long id){
        service.activateUser(id);
    }

}
