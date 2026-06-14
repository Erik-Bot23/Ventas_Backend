package com.erikjarquin.ventas.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.erikjarquin.ventas.model.entity.RoleEntity;
import com.erikjarquin.ventas.repository.RoleRepository;

@Component
@Order(1)
public class RoleBootstrap  implements CommandLineRunner {
    private final RoleRepository roleRepository;

    public RoleBootstrap(RoleRepository roleRepository){
        this.roleRepository=roleRepository;
    }

    @Override
    public void run(String...args){
        if(roleRepository.findByName("ADMIN").isEmpty()){
            RoleEntity adminRole = new RoleEntity();
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);

            System.out.println("Rol ADMIN nos creado");
        }
    }
}
