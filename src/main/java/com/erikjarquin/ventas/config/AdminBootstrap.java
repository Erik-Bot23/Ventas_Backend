package com.erikjarquin.ventas.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.erikjarquin.ventas.model.entity.RoleEntity;
import com.erikjarquin.ventas.model.entity.UserEntity;
import com.erikjarquin.ventas.repository.RoleRepository;
import com.erikjarquin.ventas.repository.UserRepository;

@Component
@Order(2)
public class AdminBootstrap implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrap(UserRepository userRepository, 
        PasswordEncoder passwordEncoder,
        RoleRepository roleRepository){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.roleRepository=roleRepository;
    }

    //Se crear el primer usuario
    @Override
    public void run(String... args){
        String adminEmail = "18jarquinsanchezerik1a@gmail.com";
        //findByEmail
        //isPresent
        boolean exists = userRepository.findByEmail(adminEmail).isPresent();

        //exists
        if(!exists){
            //orElseThrow
            //RuntimeException
            RoleEntity adminRole = roleRepository.findByName("ADMIN").orElseThrow(() ->
                new RuntimeException("Rol ADMIN no encontrado"));

            UserEntity admin = new UserEntity();
            admin.setName("Erik");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("1234"));
            admin.setRole(adminRole);
            admin.setActive(true);

            userRepository.save(admin);
            System.out.println("ADMIN INICIAL CREADO");
        }
    }
}
