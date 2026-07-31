package com.erikjarquin.ventas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erikjarquin.ventas.model.entity.PermissionEntity;
import com.erikjarquin.ventas.model.enums.PermissionName;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long>{
    //Optional<PermissionEntity> findByName(String name);
    Optional<PermissionEntity> findByName(PermissionName name);
}
