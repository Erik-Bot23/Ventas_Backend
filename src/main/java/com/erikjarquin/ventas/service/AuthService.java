package com.erikjarquin.ventas.service;

import com.erikjarquin.ventas.model.dto.Login.LoginRequest;
import com.erikjarquin.ventas.model.dto.Login.LoginResponse;
import com.erikjarquin.ventas.model.dto.ResetPassword.ChangePasswordRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
    void changePassword(String email, ChangePasswordRequest request);
}
