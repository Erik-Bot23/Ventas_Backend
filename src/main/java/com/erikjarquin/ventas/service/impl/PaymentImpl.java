package com.erikjarquin.ventas.service.impl;

import java.time.LocalDateTime;
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
import com.erikjarquin.ventas.model.enums.PaymentMethod;
import com.erikjarquin.ventas.model.enums.PaymentStatus;
import com.erikjarquin.ventas.repository.PaymentRepository;
import com.erikjarquin.ventas.repository.SaleRepository;
import com.erikjarquin.ventas.service.PaymentService;
import com.erikjarquin.ventas.service.TerminalService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
//@RequiredArgsConstructor
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
    public CardPaymentResponse processCardPayment(CardPaymentRequest request){
        log.info("Procesando pago con tarjeta para la venta ID: {}", request.getSaleId());
        
        try{
            // Validar que la venta exista
            SaleEntity sale = saleRepository.findById(request.getSaleId()).orElseThrow(
                            () -> new PaymentException("Venta no encontrada con ID" + request.getSaleId()));

            // Validar que la venta esté pendiente de pago
            if(sale.getPaymentStatus() == PaymentStatus.APPROVED){
                throw new PaymentException("Este venta ya fue pagada");
            }

            // Validar el método de pago
            if(request.getPaymentMethod() != PaymentMethod.DEBIT &&
                request.getPaymentMethod() != PaymentMethod.CREDIT){
                    throw new PaymentException("Método de pago inválido para tarjeta");
            }

            // Crear registro de pago
            PaymentEntity payment = createPendingPayment(sale, request);

            // Construir request sin datos sensibles
            TerminalRequest terminalRequest = TerminalRequest.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .amount(sale.getTotal())
                    .paymentMethod(request.getPaymentMethod().name())
                    .merchantId(terminalConfig.getMerchantId())
                    .terminalId(terminalConfig.getTerminalId())
                    .build();

            // Procesar con terminal
            TerminalResponse terminalResponse = terminalService.processPayment(terminalRequest);

            // Procesar respuesta de la terminal
            if(terminalResponse.isApproved()){
                payment.setStatus(PaymentStatus.APPROVED);
                payment.setAuthorizarionCode(terminalResponse.getAuthorizationCode());
                payment.setLastfourDigits(terminalResponse.getLastFourDigits());
                log.info("Pago aprobado. Código: {}", terminalResponse.getAuthorizationCode());
                //Guardar más información de la terminal si es necesario
            } else{
                payment.setStatus(PaymentStatus.REJECTED);
                payment.setErrorMessage(terminalResponse.getErrorMessage());
                log.warn("Pago rechazado: {}", terminalResponse.getErrorMessage());
            }

            // Guardar el pago y actualizar la venta
            PaymentEntity savedPayment = paymentRepository.save(payment);
            sale.setPaymentStatus(savedPayment.getStatus());
            sale.setPaymentMethod(request.getPaymentMethod());
            saleRepository.save(sale);

            log.info("Pago procesado exitosamente. ID: {}, Estado: {}", savedPayment.getId(), savedPayment.getStatus());

            // Retornar la respuesta
            return paymentMapper.toCardPaymentResponse(savedPayment);
            
        } catch(PaymentException e){
            log.info("Error de negocio al procesar pago: {}", e.getMessage());
            throw e;
        } catch(Exception e){
            log.info("Error inesperado al procesar pago", e);
            throw new PaymentException("Error al procesar el pago: " + e.getMessage());
        }
    }
       
    private PaymentEntity createPendingPayment(SaleEntity sale, CardPaymentRequest request){
        PaymentEntity payment = new PaymentEntity();
        payment.setSale(sale);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(sale.getTotal());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());
        return payment;
    }
}
