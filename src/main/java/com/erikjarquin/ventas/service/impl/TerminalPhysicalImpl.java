package com.erikjarquin.ventas.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
@RequiredArgsConstructor //
@ConditionalOnProperty(name = "payment.terminal.type", havingValue = "PHYSICAL") //
public class TerminalPhysicalImpl implements TerminalService {
    private final TerminalConfig config;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    //Procesar el pago
    @Override
    public TerminalResponse processPayment(TerminalRequest request){
        log.info("[TERMINAL FÍSICA] Conectando a {}:{}", config.getHost(), config.getPort());

        log.info("Monto: {}, Transacción: {}", request.getAmount(), request.getTransactionId());

        try {
            // Usar timeout
            Future<String> future = executor.submit(() -> sendToTerminal(request));

            // Esperar respuesta con timeout configurado
            String response = future.get(config.getTimeout(), TimeUnit.SECONDS);

            // Parsear respuesta por la terminal
            return parseTerminalResponse(response);

        } catch (TimeoutException e) {
            log.error("TIMEOUT: La terminal no respondió en {} segundos", config.getTimeout());

            return TerminalResponse.builder()
                                    .approved(false)
                                    .responseCode("998")
                                    .responseMessage("TIMEOUT")
                                    .errorMessage("La terminal no respondió en el tiempo establecido")
                                    .build();
        } catch (Exception e){
            log.error("Error al comunicarse con la terminal física", e);

            return TerminalResponse.builder()
                                    .approved(false)
                                    .responseCode("999")
                                    .responseMessage("ERROR DE COMUNICACIÓN")
                                    .errorMessage("No se pudo conectar con la terminal: " + e.getMessage())
                                    .build();
                                    
        }
    }

    //Enviar información a la terminal
    private String sendToTerminal(TerminalRequest request) throws IOException {
        //Protocolo seguro: Solo transactionId y amount
        String message = buildTerminalMessage(request);
        
        try(Socket socket = new Socket()) {
            //Configurar timeout del socket
            socket.connect(new InetSocketAddress(config.getHost(), config.getPort()), config.getTimeout() * 1000);
            socket.setSoTimeout(config.getTimeout() * 1000);
            
            try(PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
                // Enviar mensaje
                out.println(message);
                log.info("Mensaje enviado: {}", message);
                
                // Leer en una sola línea 
                String response = in.readLine();
            
                if(response == null){
                    throw new IOException("La terminal cerró la conexción sin responder");
                }
                
                log.info("Respuesta recibida: {}", response);
                return response;
            }
        } catch (SocketTimeoutException e){
            throw new IOException("Timeout esperando respueesta de la terminal " + e);
        }
    }

    //Mensaje de la terminal con información del pago
    private String buildTerminalMessage(TerminalRequest request){
        //Protocolo específico de la terminal (ejemplo ficticio)
        //Solo información necesaria, sin datos sensibles
        return String.format("PAY|%s|%s|%.2f|%s|%s|%s|%s", 
                        config.getMerchantId(),
                        config.getTerminalId(),
                        request.getAmount(),
                        request.getPaymentMethod(),
                        request.getTransactionId());
    }

    //Parsear la respuesta de la terminal según su protocolo
    private TerminalResponse parseTerminalResponse(String response){
        //Ejemplo de respuesta esperada:
        //"000|AUT20231201123456|1234|VISA|DEBIT|APROBADA"
        String[] parts = response.split("\\|");

        return TerminalResponse.builder()
                .approved("000".equals(parts[0]))
                .authorizationCode(parts.length > 1 ? parts[1] : null)
                .lastFourDigits(parts.length > 2 ? parts[2] : null)
                .cardBrand(parts.length > 3 ? parts[3] : null)
                .cardType(parts.length > 4 ? parts[4] : null)
                .responseCode(parts[0])
                .responseMessage(parts.length > 5 ? parts[5] : "")
                .transactionDate(LocalDateTime.now())
                .transactionId(UUID.randomUUID().toString())
                .build();
    }

    //Implementar reversa para la terminal física
    @Override
    public boolean reversePayment(String transactionId){
        log.info("Reversando transacción en terminal física: {}", transactionId);

        try {
            //Similar a processPayment pero para cancelar
            String message = String.format("REV|%s|%s|%s",
                    config.getMerchantId(),
                    config.getTerminalId(),
                    transactionId);

            return true;
        } catch (Exception e) {
            log.error("Error en reversa de pago", e);
            return false;
        }
    }

    //Consultar estado sin datos sensibles
    @Override
    public TerminalResponse getTransactionStatus(String transactionId){  
        try {
            String message = String.format("STS|%s|%s|%s",
                    config.getMerchantId(),
                    config.getTerminalId(),
                    transactionId);
            
                    // Enviar y procesar respuesta...
            return TerminalResponse.builder()
                    .approved(true)
                    .transactionId(transactionId)
                    .responseCode("000")
                    .responseMessage("TRANSACCIÓN APROBADA")
                    .transactionDate(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("Error, consultando estado", e);
            
            return TerminalResponse.builder()
                    .approved(false)
                    .transactionId(transactionId)
                    .responseMessage("ERROR")
                    .build();
        }
    }
}
