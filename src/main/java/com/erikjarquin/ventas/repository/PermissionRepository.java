package com.erikjarquin.ventas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erikjarquin.ventas.model.entity.PermissionEntity;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long>{
    Optional<PermissionEntity> findByName(String name);
}
