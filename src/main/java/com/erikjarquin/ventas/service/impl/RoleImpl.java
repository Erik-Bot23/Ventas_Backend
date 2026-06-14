package com.erikjarquin.ventas.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.erikjarquin.ventas.mapper.RoleMapper;
import com.erikjarquin.ventas.model.dto.RoleDto;
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
}

