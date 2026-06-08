package com.erikjarquin.ventas.service;

import java.util.List;

import com.erikjarquin.ventas.model.dto.CreateUserRequest;
import com.erikjarquin.ventas.model.dto.UpdateUserRequest;
import com.erikjarquin.ventas.model.dto.UserDto;

//Modulo de usuario completo y carpetas correctas
public interface  UserService {
    List<UserDto> getAllUsers();

    UserDto createUser(CreateUserRequest request);

    UserDto updateUser(Long id, UpdateUserRequest request);

    void deactivateUser(Long id);
}
