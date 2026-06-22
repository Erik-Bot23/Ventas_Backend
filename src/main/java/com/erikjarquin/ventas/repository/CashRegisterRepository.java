package com.erikjarquin.ventas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erikjarquin.ventas.model.entity.CashRegisterEntity;

public interface CashRegisterRepository extends JpaRepository<CashRegisterEntity, Long> {
    Optional<CashRegisterEntity> findByActiveTrue();
}
