package com.erikjarquin.ventas.service.impl;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.erikjarquin.ventas.config.TerminalConfig;
import com.erikjarquin.ventas.model.dto.Terminal.TerminalRequest;
import com.erikjarquin.ventas.model.dto.Terminal.TerminalResponse;
import com.erikjarquin.ventas.model.dto.Terminal.TestCard;
import com.erikjarquin.ventas.repository.TestCardRepository;
import com.erikjarquin.ventas.service.TerminalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "payment.terminal.type", havingValue = "SIMULATED", matchIfMissing = true)
public class TerminalImpl implements TerminalService {
    private final TerminalConfig config;
    private final TestCardRepository cardRepository;
    private final Random random = new Random();

    //Modo de simulación: "DETERMINISTIC" o "RANDOM"
    private static final String SIMULATION_MODE = System.getProperty("payment.terminal.simulation.mode", "DETERMINISTIC");
    
    @Override
    public TerminalResponse processPayment(TerminalRequest request){
        log.info("[Terminal simulada] Procesando pago para venta ID: {}", request.getSaleId());
        log.info("Monto: ${}, Modo: {}", request.getAmount(), SIMULATION_MODE);

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

        //Si el modo es DETERMINISTIC, user tarejtas ficticias
        if("DETERMINISTIC".equalsIgnoreCase(SIMULATION_MODE)){
            return processWithTestCard(request);
        } else {
            //Modo RANDOM (aleatorio) - comportamiento original
            return processRandom(request);
        }
    }

    /*Procesamiento determista con tarjetas ficticias*/
    private TerminalResponse processWithTestCard(TerminalRequest request){
        //1. Obtener tarjeta según el número (usamos un identificador basado en el transactionId)
        String cardNumber = getCardNumberFromTransaction(request.getTransactionId());
        TestCard card = cardRepository.findByCardNumber(cardNumber);

        if(card == null){
            log.warn("Tarjeta no encontrada: {}", cardNumber);
            return TerminalResponse.builder()
                                    .approved(false)
                                    .responseCode("099")
                                    .responseMessage("TARJETA NO REGISTRADA")
                                    .errorMessage("Tarjeta no encontrada en el sistema de pruebas")
                                    .transactionDate(LocalDateTime.now())
                                    .transactionId(request.getTransactionId())
                                    .build();
        }

        log.info("Tarjeta seleccionada: {} - Status: {}, Balance: ${}", card.getCardNumber(), card.getStatus(), card.getBalance());

        //2. Validar estado de la tarjeta
        if("BLOCKED".equalsIgnoreCase(card.getStatus())){
            log.warn("Tarjeta bloqueada");
            return TerminalResponse.builder()
                                    .approved(false)
                                    .responseCode("041")
                                    .responseMessage("TARJETA BLOQUEADA")
                                    .errorMessage("La tarjeta ha sido bloqueada por el banco")
                                    .transactionDate(LocalDateTime.now())
                                    .transactionId(request.getTransactionId())
                                    .build();
        }

        //3. Validar PIN (simulado, asumimos que el cliente ingresa el PIN correcto)
        //En simulación, asumimos que el PIN ingresado en la terminal es correcto
        //a menos que la tarjeta tenga un PIN incorrecto configurado (para simular error)
        if(!"1234".equals(card.getPin())){
            log.warn("PIN incorrecto para tarjeta: {}", card.getCardNumber());
            return TerminalResponse.builder()
                                    .approved(false)
                                    .responseCode("055")
                                    .responseMessage("PIN INCORRECTO")
                                    .errorMessage("El PIN ingresado no es válido")
                                    .transactionDate(LocalDateTime.now())
                                    .transactionId(request.getTransactionId())
                                    .build();
        }

        //4. Validar saldo insuficiente
        if(card.getBalance().compareTo(request.getAmount()) < 0){
            log.warn("Saldo insuficiente: Disponible ${}, Necesario ${}", card.getBalance(), request.getAmount());
            return TerminalResponse.builder()
                                    .approved(false)
                                    .responseCode("051")
                                    .responseMessage("SALDO INSUFICIENTE")
                                    .errorMessage("No hay suficiente saldo en la cuenta")
                                    .transactionDate(LocalDateTime.now())
                                    .transactionId(request.getTransactionId())
                                    .build();
        }

        //5. Simular comunicación con el banco (solo para darle realismo)
        if(!simulateBankCommunication()){
            return TerminalResponse.builder()
                                    .approved(false)
                                    .responseCode("098")
                                    .responseMessage("ERROR DE COMUNICACIÓN")
                                    .errorMessage("No se pudo conectar con el banco emisor")
                                    .transactionDate(LocalDateTime.now())
                                    .transactionId(request.getTransactionId())
                                    .build();
        }

        //Todo ok - Transacción aprobada
        //Descontar saldo de la tarjeta (solo simulación)
        card.setBalance(card.getBalance().subtract(request.getAmount()));
        log.info("Transacción aprobada. Nuevo saldo: ${}", card.getBalance());

        return TerminalResponse.builder()
                                .approved(true)
                                .authorizationCode(generateAuthorizationCode())
                                .lastFourDigits(getLastFourDigits(card.getCardNumber()))
                                .cardBrand(card.getBrand())
                                .cardType(card.getCardType())
                                .responseCode("000")
                                .responseMessage("APROBADA")
                                .transactionDate(LocalDateTime.now())
                                .transactionId(request.getTransactionId())
                                .build();
    }

    /*Procesamiento aleatorio (modo RANDOM) - comportamiento original*/
    private TerminalResponse processRandom(TerminalRequest request){
        boolean approved = random.nextDouble() < 0.90;

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

    /*Genera un número de tarjeta ficticio basado en el transactionId
     *Así aseguramos que cada transacción use una tarjeta específica de forma determinista 
    */
    private String getCardNumberFromTransaction(String transactionId){
        //Lista de tarjetas disponibles
        String[] cardNumbers = {
            "4111111111111111", // Tarjeta A (saldo suficiente)
            "4000000000000002", // Tarjeta B (saldo insuficiente)
            "5500000000000003", // Tarjeta C (bloqueada)
            "3400000000000004"  // Tarjeta D (PIN incorrecto)
        };

        //Usar el hash del transactionId para seleccionar una tarjeta
        int index = Math.abs(transactionId.hashCode() % cardNumbers.length);
        return cardNumbers[index];
    }

    private boolean simulateBankCommunication(){
        //Simulación: 98% de comunicación
        return random.nextDouble() < 0.98;
    }

    private String generateAuthorizationCode(){
        return "AUT" + System.currentTimeMillis() + String.format("%04d", random.nextInt(10000));
    }

    private String getLastFourDigits(String cardNumber){
        if(cardNumber == null || cardNumber.length() < 4){
            return "****";
        }
        return cardNumber.substring(cardNumber.length() - 4);
    }

    @Override
    public boolean reversePayment(String transactionId){
        log.info("Reversando transacción: {}", transactionId);
        
        //Simulación: siempre exitosa
        return true;
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
