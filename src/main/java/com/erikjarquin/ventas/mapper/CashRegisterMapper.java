package com.erikjarquin.ventas.mapper;

import org.springframework.stereotype.Component;

import com.erikjarquin.ventas.model.dto.CashResponse;
import com.erikjarquin.ventas.model.entity.CashRegisterEntity;

@Component
public class CashRegisterMapper {
    public CashResponse toResponse(CashRegisterEntity cash){
        if(cash == null){
            return null;
        }

        CashResponse response = new CashResponse();

        response.setId(cash.getId());
        response.setOpenedAt(cash.getOpenedAt());
        response.setClosedAt(cash.getClosedAt());
        response.setOpeningAmount(cash.getOpeningAmount());
        response.setClosingAmount(cash.getClosingAmount());
        response.setActive(cash.getActive());

        return response;
    }
}
