package com.erikjarquin.ventas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erikjarquin.ventas.model.entity.RoleEntity;

//Repositotio de role
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    //Encontrar role por nombre
    Optional<RoleEntity> findByName(String name);
    
    //Revisar existencia
    boolean existsByName(String name);
}
