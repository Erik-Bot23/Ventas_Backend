package com.erikjarquin.ventas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erikjarquin.ventas.model.dto.Login.LoginRequest;
import com.erikjarquin.ventas.model.dto.Login.LoginResponse;
import com.erikjarquin.ventas.model.dto.ResetPassword.ChangePasswordRequest;
import com.erikjarquin.ventas.model.dto.ResetPassword.ForgotPasswordRequest;
import com.erikjarquin.ventas.model.dto.ResetPassword.ResetPasswordRequest;
import com.erikjarquin.ventas.model.entity.UserEntity;
import com.erikjarquin.ventas.service.AuthService;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service){
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return service.login(request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest req){
        service.forgotPassword(req.getEmail());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest req){
        service.resetPassword(req.getToken(), req.getNewPassword());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest req, Authentication auth){
        UserEntity user = (UserEntity) auth.getPrincipal();

        service.changePassword(user.getEmail(), req);

        return ResponseEntity.ok().build();
    }

    //Prueba de permisos
    @GetMapping("/me/authorities")
    public Object authorities(Authentication auth){

        if(auth == null){
            return "NO AUTHENTICATED";
        }

        return auth.getAuthorities();
    }
}
