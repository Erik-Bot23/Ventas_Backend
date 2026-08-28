package com.erikjarquin.ventas.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erikjarquin.ventas.config.TerminalConfig;
import com.erikjarquin.ventas.exceptions.PaymentException;
import com.erikjarquin.ventas.mapper.PaymentMapper;
import com.erikjarquin.ventas.model.dto.Payment.CardPaymentRequest;
import com.erikjarquin.ventas.model.dto.Payment.CardPaymentResponse;
import com.erikjarquin.ventas.model.dto.Terminal.TerminalRequest;
import com.erikjarquin.ventas.model.dto.Terminal.TerminalResponse;
import com.erikjarquin.ventas.model.entity.PaymentEntity;
import com.erikjarquin.ventas.model.entity.SaleEntity;
import com.erikjarquin.ventas.model.enums.PaymentStatus;
import com.erikjarquin.ventas.repository.PaymentRepository;
import com.erikjarquin.ventas.repository.SaleRepository;
import com.erikjarquin.ventas.service.PaymentService;
import com.erikjarquin.ventas.service.TerminalService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentImpl implements PaymentService {
    private final SaleRepository saleRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final TerminalService terminalService;
    private final TerminalConfig terminalConfig;

    public PaymentImpl(SaleRepository saleRepository,
                        PaymentRepository paymentRepository,
                        PaymentMapper paymentMapper,
                        TerminalService terminalService,
                        TerminalConfig terminalConfig){
                            this.saleRepository=saleRepository;
                            this.paymentRepository=paymentRepository;
                            this.paymentMapper=paymentMapper;
                            this.terminalService=terminalService;
                            this.terminalConfig=terminalConfig;
                        }

    @Override
    @Transactional
    public CardPaymentResponse processCardPayment(CardPaymentRequest request){
        log.info("Procesando pago con tarjeta para la venta ID: {}", request.getSaleId());
        
        //SIN TRANSACCIONAL PROPIO (usa el de SaleImpl)
        //SIN MANEJO DE STOCK (responsabilidad de SaleImpl)
        //Validar que la venta exista
        SaleEntity sale = saleRepository.findById(request.getSaleId()).orElseThrow(
                        () -> new PaymentException("Venta no encontrada con ID" + request.getSaleId()));

        //Validación de idempotencia
        Optional<PaymentEntity> existingPayment = paymentRepository.findBySaleId(sale.getId());

        if(existingPayment.isPresent()){
            PaymentEntity payment = existingPayment.get();

            //Si ya está aprobado, retornar éxito
            if(payment.getStatus() == PaymentStatus.APPROVED){
                log.info("Venta ya pagada. Payment ID: {}", payment.getId());
                return buildSuccessResponse(payment);
            }

            //Si esta pendiente, verificar si debemos consulyar estado
            if(payment.getStatus() == PaymentStatus.PENDING){
                //Si ya consultamos estado recientemente (últimos 30 segundos)
                if(payment.isStatusQueried() && payment.getLastStatusQuery() != null && payment.getLastStatusQuery().isAfter(LocalDateTime.now().minusSeconds(30))){
                    log.warn("Transacción en proceso, no reintentar. TransactionId: {}", payment.getTransactionId());
                    throw new PaymentException("La transacción está en proceso. Por favor espere.");
                }
                

                //Consultar estado actual en la terminal
                log.info("Consultando estado de transacción pendiente: {}", payment.getTransactionId());
                TerminalResponse statusResponse = terminalService.getTransactionStatus(payment.getTransactionId());

                payment.setStatusQueried(true);
                payment.setLastStatusQuery(LocalDateTime.now());

                if(statusResponse.isApproved()){
                    payment.setStatus(PaymentStatus.APPROVED);
                    payment.setAuthorizarionCode(statusResponse.getAuthorizationCode());
                    payment.setLastfourDigits(statusResponse.getAuthorizationCode());
                    paymentRepository.save(payment);

                    //Actualizar venta
                    sale.setPaymentStatus(PaymentStatus.APPROVED);
                    saleRepository.save(sale);

                    log.info("Transacción confirmada etosamente. ID: {}", payment.getId());

                    return buildSuccessResponse(payment);
                } else {
                    //Si la terminal dice que no está aprobada, rechazar
                    payment.setStatus(PaymentStatus.REJECTED);
                    payment.setErrorMessage(statusResponse.getErrorMessage());
                    paymentRepository.save(payment);

                    sale.setPaymentStatus(PaymentStatus.REJECTED);
                    saleRepository.save(sale);

                    throw new PaymentException("Pago rechazado: " + statusResponse.getErrorMessage());
                }
            }

            //Si est rechazado, no permitir reintentar
            if(payment.getStatus() == PaymentStatus.REJECTED){
                throw new PaymentException("Esta venta ya fue rechazada. Motivo: " + payment.getErrorMessage());
            }
        }

        //Crear nuevo pago con transaction ID persistente 
        PaymentEntity payment = new PaymentEntity();
        payment.setSale(sale);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(sale.getTotal());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setAttempCount(0);
        payment.setStatusQueried(false);

        //Generar y guardar transaction ID
        String transactionId = generateTransactionId(sale.getId());
        payment.setTransactionId(transactionId);

        //Guardar pago para tener el transactionId persistente
        try{
            payment = paymentRepository.save(payment);
        } catch(Exception e){
            //Si hay duplicado, es porque otro hilo creó el pago
            log.warn("Conflicto de concurrencia al crear pago");
            return processCardPayment(request); //Reintentar (recursivo, pero con validación)
        }

        //Asociación bidireccional
        sale.setPayment(payment);
        sale.setPaymentStatus(PaymentStatus.PENDING);
        saleRepository.save(sale);

        log.info("Nuevo pago creado. TransactionId: {}", transactionId);

        //Construir request para terminal
        TerminalRequest terminalRequest = TerminalRequest.builder()
                        .transactionId(transactionId)
                        .amount(sale.getTotal())
                        .paymentMethod(request.getPaymentMethod().name())
                        .merchantId(terminalConfig.getMerchantId())
                        .terminalId(terminalConfig.getTerminalId())
                        .build();

        //Procesar con terminal
        try{
            TerminalResponse terminalResponse = terminalService.processPayment(terminalRequest);

            //Actualizar pago según respuesta
            if(terminalResponse.isApproved()){
                payment.setStatus(PaymentStatus.APPROVED);
                payment.setAuthorizarionCode(terminalResponse.getAuthorizationCode());
                payment.setLastfourDigits(terminalResponse.getLastFourDigits());
                log.info("Pago aprobado. Código: {}", terminalResponse.getAuthorizationCode());
            } else {
                payment.setStatus(PaymentStatus.REJECTED);
                payment.setErrorMessage(terminalResponse.getErrorMessage());
                log.warn("Pago rechazado: {}", terminalResponse.getErrorMessage());
                throw new PaymentException("Pago rechazado: " + terminalResponse.getErrorMessage());
            }

            //Actualizar contador de intentos
            payment.setAttempCount(payment.getAttempCount() + 1);
            PaymentEntity savedPayment = paymentRepository.save(payment);

            //Actualizar venta
            sale.setPaymentStatus(savedPayment.getStatus());
            saleRepository.save(sale);

            return buildSuccessResponse(savedPayment);
        } catch(Exception e) {
            log.error("Error procesando pago con terminal", e);

            //En caso de error, el transaction id queda persistido
            //Podemos consultar estado después
            payment.setErrorMessage("Error de comunicación: " + e.getMessage());
            payment.setAttempCount(payment.getAttempCount() + 1);
            paymentRepository.save(payment);

            throw new PaymentException("Error al procesar el pago: " + e.getMessage());
        }   
    }

    /**
     * Genera un transactionId único y determinista badado en el ID de la venta
     */
    private String generateTransactionId(Long saleId){
        //Formato: TXN-{saleId}-{timestamp}-{uuid_corto}
        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortUuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("TXN-%d-%s-%s", saleId, timestamp, shortUuid);
    }

    private CardPaymentResponse buildSuccessResponse(PaymentEntity payment){
        CardPaymentResponse response = new CardPaymentResponse();
        response.setPaymentId(payment.getId());
        response.setSaleId(payment.getSale().getId());
        response.setStatus(payment.getStatus());
        response.setAuthorizationCode(payment.getAuthorizationCode());
        response.setAmount(payment.getAmount());
        response.setPaymentDate(payment.getPaymentDate());
        response.setTransactionId(payment.getTransactionId());
        response.setMessage("Pago aprobado con éxito");
        return response;
    }

    @Override
    public CardPaymentResponse getPaymentStatus(String transactionId){
        log.info("Consultando estado de transacción: {}", transactionId);

        PaymentEntity payment = paymentRepository.findByTransactionId(transactionId).orElseThrow(
                                () -> new PaymentException("Transacción no encontrada"));
        
        //Si está pendiente, consultar estado actual
        if(payment.getStatus() == PaymentStatus.PENDING){
            TerminalResponse statuResponse = terminalService.getTransactionStatus(transactionId);

            payment.setStatusQueried(true);
            payment.setLastStatusQuery(LocalDateTime.now());

            if(statuResponse.isApproved()){
                payment.setStatus(PaymentStatus.APPROVED);
                payment.setAuthorizarionCode(statuResponse.getAuthorizationCode());
                payment.setLastfourDigits(statuResponse.getLastFourDigits());
                paymentRepository.save(payment);

                //Actualizar venta
                SaleEntity sale = payment.getSale();
                sale.setPaymentStatus(PaymentStatus.APPROVED);
                saleRepository.save(sale);
            }
        }

        return buildSuccessResponse(payment);
    } 
}
