package com.erikjarquin.ventas.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.erikjarquin.ventas.model.entity.UserEntity;
import com.erikjarquin.ventas.model.enums.Role;
import com.erikjarquin.ventas.repository.UserRepository;

@Component
public class AdminBootstrap implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrap(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public void run(String... args){
        String adminEmail = "admin@ventas.com";
        boolean exists = userRepository.findByEmail(adminEmail).isPresent();

        if(!exists){
            UserEntity admin = new UserEntity();
            admin.setName("Erik");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("1234"));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);

            userRepository.save(admin);
            System.out.println("ADMIN INICIAL CREADO");
        }
    }
}
