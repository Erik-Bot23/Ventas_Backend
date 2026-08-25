package com.erikjarquin.ventas.repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.erikjarquin.ventas.model.dto.Terminal.TestCard;

public class TestCardRepository {
    private final Map<String, TestCard> cards = new HashMap<>();

    public TestCardRepository(){
        // Tarjeta A -Saldo insuficiente
        cards.put("4111111111111111", TestCard.builder()
                    .cardNumber("4111111111111111")
                    .brand("VISA")
                    .cardType("DEBIT")
                    .balance(BigDecimal.valueOf(10000))
                    .status("ACTIVE")
                    .pin("1234")
                    .build());
    }

    
}
