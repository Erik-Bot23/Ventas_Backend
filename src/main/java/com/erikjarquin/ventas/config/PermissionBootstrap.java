package com.erikjarquin.ventas.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.erikjarquin.ventas.model.entity.PermissionEntity;
import com.erikjarquin.ventas.model.enums.PermissionName;
import com.erikjarquin.ventas.repository.PermissionRepository;
import com.erikjarquin.ventas.repository.RoleRepository;

import jakarta.websocket.OnClose;

@Component
@Order(3)
public class PermissionBootstrap implements CommandLineRunner {
    private final PermissionRepository permissionRepository;
    //private final RoleRepository roleRepository;

    public PermissionBootstrap(
        PermissionRepository permissionRepository){
            this.permissionRepository=permissionRepository;
        }
    
    @Override
    public void run(String...args) throws Exception{
        if(permissionRepository.count() > 0){
            return;
        }

        List<PermissionEntity> permissions = Arrays.stream(
            PermissionName.values()).map(permission -> new PermissionEntity(null, permission)).toList();

        permissionRepository.saveAll(permissions);
    }
}
