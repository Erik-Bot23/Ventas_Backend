package com.erikjarquin.ventas.service;

import com.erikjarquin.ventas.model.dto.LoginRequest;
import com.erikjarquin.ventas.model.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
