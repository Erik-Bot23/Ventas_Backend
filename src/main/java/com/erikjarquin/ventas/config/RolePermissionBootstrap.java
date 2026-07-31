package com.erikjarquin.ventas.config;

import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.erikjarquin.ventas.model.entity.PermissionEntity;
import com.erikjarquin.ventas.model.entity.RoleEntity;
import com.erikjarquin.ventas.model.enums.PermissionName;
import com.erikjarquin.ventas.repository.PermissionRepository;
import com.erikjarquin.ventas.repository.RoleRepository;

@Component
public class RolePermissionBootstrap implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RolePermissionBootstrap(RoleRepository roleRepository, PermissionRepository permissionRepository){
        this.roleRepository=roleRepository;
        this.permissionRepository=permissionRepository;
    }

    @Override
    public void run(String...args){
        RoleEntity admin = roleRepository.findByName("ADMIN").orElseThrow();
        RoleEntity cajero = roleRepository.findByName("CAJERO").orElseThrow();
        RoleEntity almacenista = roleRepository.findByName("ALMACENISTA").orElseThrow();

        List<PermissionEntity> allPermissions = permissionRepository.findAll();

        admin.setPermissions(allPermissions);

        cajero.setPermissions(filterPermissions(
            PermissionName.VER_PRODUCTOS,
            PermissionName.VER_VENTAS,
            PermissionName.CREAR_VENTAS,
            PermissionName.ABRIR_CAJA,
            PermissionName.CERRAR_CAJA
        ));

        almacenista.setPermissions(filterPermissions(
            PermissionName.VER_PRODUCTOS,
            PermissionName.CREAR_PRODUCTOS,
            PermissionName.EDITAR_PRODUCTOS
        ));

        roleRepository.save(admin);
        roleRepository.save(cajero);
        roleRepository.save(almacenista);
    }

    private List<PermissionEntity> filterPermissions(PermissionName...permissionNames){
        Set<PermissionName> requested = Set.of(permissionNames);
        return permissionRepository.findAll().stream().filter(permission -> requested.contains(permission.getName())).toList();
    }
}
