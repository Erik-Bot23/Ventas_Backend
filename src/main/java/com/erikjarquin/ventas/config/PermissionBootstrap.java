package com.erikjarquin.ventas.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;

import com.erikjarquin.ventas.model.entity.PermissionEntity;
import com.erikjarquin.ventas.repository.PermissionRepository;
import com.erikjarquin.ventas.repository.RoleRepository;

public class PermissionBootstrap implements CommandLineRunner {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public PermissionBootstrap(
        PermissionRepository permissionRepository,
        RoleRepository roleRepository){
            this.permissionRepository=permissionRepository;
            this.roleRepository=roleRepository;
        }
    
    public void run(String...args) throws Exception{
        if(permissionRepository.count() > 0){
            return;
        }

        List<PermissionEntity> permissions = List.of(
            new PermissionEntity(null, "VER_PRODUCTOS"),
            new PermissionEntity(null, "CREAR_PRODUCTOS"),
            new PermissionEntity(null, "EDITAR_PRODUCTOS"),
            new PermissionEntity(null, "ELIMINAR_PRODUCTOS"),

            new PermissionEntity(null, "VER_USUARIOS"),
            new PermissionEntity(null, "CREAR_USUARIOS"),
            new PermissionEntity(null, "EDITAR_USUARIOS"),
            new PermissionEntity(null, "ELIMINAR_USUARIOS"),

            new PermissionEntity(null, "VER_VENTAS"),
            new PermissionEntity(null, "CREAR_VENTAS"),
            new PermissionEntity(null, "CANCELAR_VENTAS"),

            new PermissionEntity(null, "ABRIR_CAJA"),
            new PermissionEntity(null, "CERRAR_CAJA"),
            new PermissionEntity(null, "VER_CORTE_CAJA"),

            new PermissionEntity(null, "VER_REPORTES"),
            new PermissionEntity(null, "EXPORTAR_REPORTES"),
            new PermissionEntity(null, "VER_CONFIGURACION"),
            new PermissionEntity(null, "EDITAR_CONFIGURACION")

        );

        permissionRepository.saveAll(permissions);
    }
}
