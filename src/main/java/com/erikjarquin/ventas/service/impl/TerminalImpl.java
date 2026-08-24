package com.erikjarquin.ventas.service.impl;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.erikjarquin.ventas.config.TerminalConfig;
import com.erikjarquin.ventas.model.dto.Terminal.TerminalRequest;
import com.erikjarquin.ventas.model.dto.Terminal.TerminalResponse;
import com.erikjarquin.ventas.service.TerminalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "payment.terminal.type", havingValue = "SIMULATED", matchIfMissing = true)
public class TerminalImpl implements TerminalService {
    private final TerminalConfig config;
    private final Random random = new Random();

    @Override
    public TerminalResponse processPayment(TerminalRequest request){
        log.info("[Terminal simulada] Procesando pago para venta ID: {}", request.getSaleId());
        log.info("Monto: ${}", request.getAmount());

        //Simular tiempo de procesamiento (con timeout)
        try{
            int delay = 500 + random.nextInt(1000);

            if(delay > config.getTimeout() * 1000){
                delay = config.getTimeout() * 1000 / 2;
            }

            Thread.sleep(delay);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }

        //Simular respuesta sin datos de tarjeta
        boolean approved = random.nextDouble() < 0.90; // 90% de aprobación

        if(approved){
            return TerminalResponse.builder()
                    .approved(true)
                    .authorizationCode("AUT" + System.currentTimeMillis())
                    .lastFourDigits(String.format("%04d", random.nextInt(10000)))
                    .cardBrand(random.nextBoolean() ? "VISA" : "MASTERCARD")
                    .cardType(request.getPaymentMethod())
                    .responseCode("000")
                    .responseMessage("APROBADA")
                    .transactionDate(LocalDateTime.now())
                    .transactionId(request.getTransactionId())
                    .build();
        } else {
            String[] errors = {
                "FONDOS INSUFICIENTES",
                "TARJETA BLOQUEADA",
                "LÍMITE EXCEDIDO",
                "ERROR DE COMUNICACIÓN"
            };

            String error = errors[random.nextInt(errors.length)];

            return TerminalResponse.builder()
                    .approved(false)
                    .responseCode(random.nextBoolean() ? "051" : "061")
                    .responseMessage(error)
                    .errorMessage(error)
                    .transactionDate(LocalDateTime.now())
                    .transactionId(request.getTransactionId())
                    .build();
        }
    }

    @Override
    public boolean reversePayment(String transactionId){
        log.info("Reversando transacción: {}", transactionId);

        //Simulación: 95% de exito en reversa
        return random.nextDouble() < 0.95;
    }

    @Override
    public TerminalResponse getTransactionStatus(String transactionId){
        log.info("Consultando estado de la transacción: {}", transactionId);

        //Simulación: Siempre retorna aprobado
        return TerminalResponse.builder()
                                .approved(true)
                                .transactionId(transactionId)
                                .responseCode("000")
                                .responseMessage("TRANSACCIÓN APROBADA")
                                .transactionDate(LocalDateTime.now())
                                .build();
    }
}
