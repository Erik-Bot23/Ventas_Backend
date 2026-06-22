package com.erikjarquin.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erikjarquin.ventas.model.entity.SaleEntity;

public interface SaleRepository extends JpaRepository<SaleEntity, Long> {
}