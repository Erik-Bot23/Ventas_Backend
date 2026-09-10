package com.erikjarquin.ventas.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.erikjarquin.ventas.model.entity.PermissionEntity;
import com.erikjarquin.ventas.model.enums.PermissionName;
import com.erikjarquin.ventas.repository.PermissionRepository;

//Crear los permisos
@Component
@Order(3)
public class PermissionBootstrap implements CommandLineRunner {
    private final PermissionRepository permissionRepository;

    public PermissionBootstrap(
        PermissionRepository permissionRepository){
            this.permissionRepository=permissionRepository;
        }
    
    //
    @Override
    public void run(String...args){
        for(PermissionName permissionName : PermissionName.values()){
            if(permissionRepository.findByName(permissionName).isEmpty()) {
                PermissionEntity permission = new PermissionEntity(null, permissionName);
            
                permissionRepository.save(permission);

                System.out.println("Permiso creado: " + permissionName);
            }
        }
    }
}
