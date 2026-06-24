package com.erikjarquin.ventas.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.erikjarquin.ventas.mapper.CashRegisterMapper;
import com.erikjarquin.ventas.model.dto.CashResponse;
import com.erikjarquin.ventas.model.dto.CloseCashRequest;
import com.erikjarquin.ventas.model.dto.OpenCashRequest;
import com.erikjarquin.ventas.model.entity.CashRegisterEntity;
import com.erikjarquin.ventas.repository.CashRegisterRepository;
import com.erikjarquin.ventas.service.CashRegisterService;

@Service
public class CashRegisterImpl implements CashRegisterService {
    private final CashRegisterRepository repository;
    private final CashRegisterMapper mapper;

    public CashRegisterImpl(
        CashRegisterRepository repository,
        CashRegisterMapper mapper){
        this.repository = repository;
        this.mapper = mapper;
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

        return mapper.toResponse(cash);
    }

    @Override
    public CashResponse close(CloseCashRequest request){
        CashRegisterEntity cash = repository.findByActiveTrue().orElseThrow(() -> 
            new RuntimeException("No existe caja abierta"));

        cash.setClosedAt(LocalDateTime.now());
        cash.setClosingAmount(request.getClosingAmount());
        cash.setActive(false);
        repository.save(cash);

        return mapper.toResponse(cash);
    }

    @Override
    public CashResponse getActiveCash(){
        CashRegisterEntity cash = repository.findByActiveTrue().orElseThrow(() ->
            new RuntimeException("No existe la caja abierta"));

        return mapper.toResponse(cash);
    }
}
