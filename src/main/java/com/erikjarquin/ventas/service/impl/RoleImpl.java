package com.erikjarquin.ventas.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.erikjarquin.ventas.mapper.RoleMapper;
import com.erikjarquin.ventas.model.dto.UserRole.RoleDto;
import com.erikjarquin.ventas.model.entity.RoleEntity;
import com.erikjarquin.ventas.repository.RoleRepository;
import com.erikjarquin.ventas.service.RoleService;

@Service
public class RoleImpl implements RoleService {
    private final RoleRepository repository;

    public RoleImpl(RoleRepository repository){
        this.repository=repository;
    }

    @Override
    public List<RoleDto> getAllRoles(){
        return repository.findAll().stream().map(RoleMapper::toDto).toList();
    }

    @Override
    public RoleDto save(RoleDto dto){
        RoleEntity entity = RoleMapper.toEntity(dto);
        RoleEntity saved = repository.save(entity);

        return RoleMapper.toDto(saved);
    }

    @Override
    public void delete(Long id){
        RoleEntity role = repository.findById(id).orElseThrow(() -> 
        new RuntimeException("Role no encontrado"));

        if(role.getUsers() != null && !role.getUsers().isEmpty()){
            throw new RuntimeException("No puedes eliminar un role con usuarios");
        }
        repository.deleteById(id);
    }


}

