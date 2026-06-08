package com.erikjarquin.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erikjarquin.ventas.model.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
