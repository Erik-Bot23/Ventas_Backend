package com.erikjarquin.ventas.service.impl;

import java.time.LocalDateTime;

import com.erikjarquin.ventas.model.dto.CashResponse;
import com.erikjarquin.ventas.model.dto.OpenCashRequest;
import com.erikjarquin.ventas.model.entity.CashRegisterEntity;
import com.erikjarquin.ventas.repository.CashRegisterRepository;
import com.erikjarquin.ventas.service.CashRegisterService;

public class CashRegisterImpl implements CashRegisterService {
    private final CashRegisterRepository repository;

    public CashRegisterImpl(CashRegisterRepository repository){
        this.repository = repository;
    }

    @Override
    public CashResponse open(OpenCashRequest request){
        repository.findByActiveTrue().ifPresent(c -> {
            throw new RuntimeException("Ta existe una caja abierta");
        });

        CashRegisterEntity cash = new CashRegisterEntity();

        cash.setOpenedAt(LocalDateTime.now());
        cash.setOpeningAmount(request.getOpeningAmount());
        cash.setActive(true);
        repository.save(cash);

        return toResponse(cash);
    }
}
