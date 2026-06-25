package com.erikjarquin.ventas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erikjarquin.ventas.model.entity.CategoryEntity;
import com.erikjarquin.ventas.model.entity.RoleEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<RoleEntity> findByName(String name);
}
