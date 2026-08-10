package com.erikjarquin.ventas.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erikjarquin.ventas.mapper.RoleMapper;
import com.erikjarquin.ventas.model.dto.Role.CreateRoleRequest;
import com.erikjarquin.ventas.model.dto.Role.RoleDto;
import com.erikjarquin.ventas.model.dto.Role.UpdateRoleRequest;
import com.erikjarquin.ventas.model.entity.PermissionEntity;
import com.erikjarquin.ventas.model.entity.RoleEntity;
import com.erikjarquin.ventas.model.enums.PermissionName;
import com.erikjarquin.ventas.repository.PermissionRepository;
import com.erikjarquin.ventas.repository.RoleRepository;
import com.erikjarquin.ventas.service.RoleService;

@Service
public class RoleImpl implements RoleService {
    private final RoleRepository repository;
    private final PermissionRepository permissionRepository;

    public RoleImpl(RoleRepository repository, PermissionRepository permissionRepository){
        this.repository=repository;
        this.permissionRepository=permissionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles(){
        return repository.findAll().stream().map(RoleMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto getRoleById(Long id){
        RoleEntity role = repository.findById(id).orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        return RoleMapper.toDto(role);
    }

    @Override
    public RoleDto createRole(CreateRoleRequest request){
        validateRoleName(request.getName());

        if(repository.existsByName(request.getName())){
            throw new RuntimeException("Ya existe un rol con ese nombre");
        }

        Set<PermissionEntity> permissions = findPermissions(request.getPermissions());

        RoleEntity role = new RoleEntity();
        role.setName(request.getName());
        role.setPermissions(permissions);

        RoleEntity saved = repository.save(role);

        return RoleMapper.toDto(saved);
    }

    @Override
    public RoleDto updateRole(Long id, UpdateRoleRequest request){
        RoleEntity role = repository.findById(id).orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        validateRoleName(request.getName());

        if(!role.getName().equals(request.getName()) && repository.existsByName(request.getName())){
            throw new RuntimeException("Ya existe un rol con ese nombre");
        }

        Set<PermissionEntity> permissions = findPermissions(request.getPermissions());
        role.setName(request.getName());
        role.setPermissions(permissions);

        RoleEntity updated = repository.save(role);

        return RoleMapper.toDto(updated);
    }

    @Override
    public void deleteRole(Long id){
        RoleEntity role = repository.findById(id).orElseThrow(() -> 
        new RuntimeException("Role no encontrado"));

        if(role.getUsers() != null && !role.getUsers().isEmpty()){
            throw new RuntimeException("No puedes eliminar un role con usuarios");
        }
        repository.deleteById(id);
    }

    private Set<PermissionEntity> findPermissions(List<String> permissionNames){
        if(permissionNames == null || permissionNames.isEmpty()){
            return new HashSet<>();
        }

        List<PermissionName> names = permissionNames.stream().map(this::parsePermission).toList();
        List<PermissionEntity> permissions = permissionRepository.findByNameIn(names);

        if(permissions.size() != names.size()){
            throw new RuntimeException("Uno o más permisos no existen");
        }

        return new HashSet<>(permissions);
    }

    private PermissionName parsePermission(String permission){
        try {
            return PermissionName.valueOf(permission);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Permiso inválido: " + permission);
        }
    }

    private void validateRoleName(String name){
        if(name == null || name.trim().isEmpty()){
            throw new RuntimeException("El nombre del rol es obligatorio");
        }
    }

}

