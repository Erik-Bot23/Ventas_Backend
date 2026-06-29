package com.erikjarquin.ventas.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.erikjarquin.ventas.config.JwtUtil;
import com.erikjarquin.ventas.model.dto.LoginRequest;
import com.erikjarquin.ventas.model.dto.LoginResponse;
import com.erikjarquin.ventas.model.entity.UserEntity;
import com.erikjarquin.ventas.repository.UserRepository;
import com.erikjarquin.ventas.service.AuthService;

@Service
public class AuthImpl implements AuthService {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthImpl(
        UserRepository repo, 
        PasswordEncoder passwordEncoder,
        JwtUtil jwtUtil){
        this.repo=repo;
        this.passwordEncoder=passwordEncoder;
        this.jwtUtil=jwtUtil;
    }

    @Override
    public LoginResponse login(LoginRequest request){

        return repo.findByEmail(request.getEmail())
                .filter(UserEntity::isActive)
                .filter(user -> passwordEncoder.matches(
                    request.getPassword(),
                    user.getPassword()))
                .map(user -> {
                    String token = jwtUtil.generateToken(user);

                    return new LoginResponse(
                        true,
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        List.of(user.getRole().getName()),
                        token
                    ); 
                })
                .orElse(
                    new LoginResponse(
                        false,
                        null,
                        null,
                        null,
                        null,
                        null
                    )
                );
    }
}
