package com.erikjarquin.ventas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erikjarquin.ventas.model.entity.PaymentEntity;
import com.erikjarquin.ventas.model.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findBySaleId(Long saleId);
    List<PaymentEntity> findByStatus(PaymentStatus status);    
}
