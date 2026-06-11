package com.erikjarquin.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erikjarquin.ventas.model.entity.SaleDetailEntity;

public interface SaleDetailRepository extends JpaRepository<SaleDetailEntity, Long> {

}