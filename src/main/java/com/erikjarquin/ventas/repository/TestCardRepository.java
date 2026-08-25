package com.erikjarquin.ventas.repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.erikjarquin.ventas.model.dto.Terminal.TestCard;

public class TestCardRepository {
    private final Map<String, TestCard> cards = new HashMap<>();

    public TestCardRepository(){
        // Tarjeta A - Saldo suficiente
        cards.put("4111111111111111", TestCard.builder()
                    .cardNumber("4111111111111111")
                    .brand("VISA")
                    .cardType("DEBIT")
                    .balance(BigDecimal.valueOf(10000))
                    .status("ACTIVE")
                    .pin("1234")
                    .build());

        // Tarjeta B - Saldo insuficiente
        cards.put("4000000000000002", TestCard.builder()
                .cardNumber("4000000000000002")
                .brand("VISA")
                .cardType("DEBIT")
                .status("ACTIVE")
                .pin("1234")
                .build());

        //Tarjeta C - Bloqueada
        cards.put("5500000000000003", TestCard.builder()
                .cardNumber("5500000000000003")
                .brand("MASTERCARD")
                .cardType("CREDIT")
                .balance(BigDecimal.valueOf(5000))
                .status("BLOCKED")
                .pin("1234")
                .build());

        //Tarjeta D - PIN incorrecto
        cards.put("3400000000000004", TestCard.builder()
                .cardNumber("3400000000000004")
                .brand("AMEX")
                .cardType("CREDIT")
                .balance(BigDecimal.valueOf(3000))
                .status("0000")
                .build());       
    }
    
    public TestCard findByCardNumber(String cardNumber){
        return cards.get(cardNumber);
    }

    public boolean exists(String cardNumber){
        return cards.containsKey(cardNumber);
    }
}
