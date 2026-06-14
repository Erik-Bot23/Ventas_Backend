package com.erikjarquin.ventas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erikjarquin.ventas.model.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String name);
}
