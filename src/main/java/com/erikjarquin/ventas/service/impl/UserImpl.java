package com.erikjarquin.ventas.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.erikjarquin.ventas.mapper.UserMapper;
import com.erikjarquin.ventas.model.dto.User.CreateUserRequest;
import com.erikjarquin.ventas.model.dto.User.UpdateUserRequest;
import com.erikjarquin.ventas.model.dto.User.UserDto;
import com.erikjarquin.ventas.model.entity.RoleEntity;
import com.erikjarquin.ventas.model.entity.UserEntity;
import com.erikjarquin.ventas.repository.RoleRepository;
import com.erikjarquin.ventas.repository.UserRepository;
import com.erikjarquin.ventas.service.UserService;

//Modulo de usuarios completo y carpetas corregidas
@Service
public class UserImpl implements UserService {
    private final UserRepository repository;
     private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserImpl(UserRepository repository, 
        PasswordEncoder passwordEncoder,
        RoleRepository roleRepository){
        this.repository=repository;
        this.passwordEncoder=passwordEncoder;
        this.roleRepository=roleRepository;
    }

   @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream().map(UserMapper::toDto).toList();
    }

    @Override
    public UserDto createUser(CreateUserRequest request){
        UserEntity user = new UserEntity();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        RoleEntity role = roleRepository.findById(request.getRoleId()).orElseThrow(() -> 
            new RuntimeException("Rol no encontrado"));
        user.setRole(role);
        user.setActive(true);

        if(repository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("El correo ya esta registrado");
        }

        UserEntity saved = repository.save(user);

        return UserMapper.toDto(saved);

    }

    @Override
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        UserEntity user = repository.findById(id).orElseThrow(() ->
            new RuntimeException("Usuario no encontrado"));

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        RoleEntity role = roleRepository.findById(request.getRoleId()).orElseThrow(() -> 
            new RuntimeException("Rol no encontrado"));
        user.setRole(role);
        //Quitar esta línea para evitar el false en active al actualizar
        //user.setActive(request.isActive());
        UserEntity updated = repository.save(user);

        return UserMapper.toDto(updated);
    }

    @Override
    public void deactivateUser(Long id) {
        UserEntity user = repository.findById(id).orElseThrow(() ->
            new RuntimeException("Usuario no encontrado"));

        user.setActive(false);
        repository.save(user);
    }

    @Override
    public void activateUser(Long id){
        UserEntity user = repository.findById(id).orElseThrow(() ->
            new RuntimeException("Usuario no encontrado"));

        user.setActive(true);
        repository.save(user);
    }
}
