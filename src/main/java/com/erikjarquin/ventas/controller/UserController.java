package com.erikjarquin.ventas.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erikjarquin.ventas.model.dto.CreateUserRequest;
import com.erikjarquin.ventas.model.dto.UpdateUserRequest;
import com.erikjarquin.ventas.model.dto.UserDto;
import com.erikjarquin.ventas.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service){
        this.service = service;
    }

    @GetMapping
    public List<UserDto> getAllUsers(){
        return service.getAllUsers();
    }

    @PostMapping
    public UserDto createUser(@RequestBody CreateUserRequest request){
        return service.createUser(request);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request){
        return service.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivateUser(@PathVariable Long id){
        service.deactivateUser(id);
    }

    @PatchMapping("/{id}/activate")
    public void activateUser(@PathVariable Long id){
        service.activateUser(id);
    }

}
