package com.erikjarquin.ventas.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.erikjarquin.ventas.model.dto.LoginRequest;
import com.erikjarquin.ventas.model.dto.LoginResponse;
import com.erikjarquin.ventas.model.entity.UserEntity;
import com.erikjarquin.ventas.repository.UserRepository;
import com.erikjarquin.ventas.service.AuthService;

@Service
public class AuthImpl implements AuthService {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public AuthImpl(UserRepository repo, PasswordEncoder passwordEncoder){
        this.repo=repo;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public LoginResponse login(LoginRequest request){

        return repo.findByEmail(request.getEmail())
                .filter(UserEntity::isActive)
                .filter(user -> passwordEncoder.matches(
                    request.getPassword(),
                    user.getPassword()))
                .map(user -> new LoginResponse(
                    true,
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name()
                ))
                .orElse(
                    new LoginResponse(
                        false,
                        null,
                        null,
                        null,
                        null
                    )
                );
    }
}
