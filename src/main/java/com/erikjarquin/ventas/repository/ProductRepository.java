package com.erikjarquin.ventas.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erikjarquin.ventas.model.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByCategory_Name(String name);
}