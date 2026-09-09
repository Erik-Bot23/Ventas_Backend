package com.erikjarquin.ventas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erikjarquin.ventas.model.entity.CashRegisterEntity;
import com.erikjarquin.ventas.model.entity.SaleEntity;

//Repositorio de venta
public interface SaleRepository extends JpaRepository<SaleEntity, Long> {
    List<SaleEntity> findByCashRegister(CashRegisterEntity cashRegister);
}