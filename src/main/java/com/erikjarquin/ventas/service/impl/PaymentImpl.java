package com.erikjarquin.ventas.service.impl;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erikjarquin.ventas.exceptions.PaymentException;
import com.erikjarquin.ventas.mapper.PaymentMapper;
import com.erikjarquin.ventas.model.dto.Payment.CardPaymentRequest;
import com.erikjarquin.ventas.model.dto.Payment.CardPaymentResponse;
import com.erikjarquin.ventas.model.dto.Payment.PaymentValidationResult;
import com.erikjarquin.ventas.model.entity.PaymentEntity;
import com.erikjarquin.ventas.model.entity.SaleEntity;
import com.erikjarquin.ventas.model.enums.PaymentMethod;
import com.erikjarquin.ventas.model.enums.PaymentStatus;
import com.erikjarquin.ventas.repository.PaymentRepository;
import com.erikjarquin.ventas.repository.SaleRepository;
import com.erikjarquin.ventas.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
//@RequiredArgsConstructor
public class PaymentImpl implements PaymentService {
    private final SaleRepository saleRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentImpl(SaleRepository saleRepository,
                        PaymentRepository paymentRepository,
                        PaymentMapper paymentMapper){
                            this.saleRepository=saleRepository;
                            this.paymentRepository=paymentRepository;
                            this.paymentMapper=paymentMapper;
                        }

    @Override
    public CardPaymentResponse processCardPayment(CardPaymentRequest request){
        log.info("Procesando pago con tarjeta para la venta ID: {}", request.getSaleId());
        
        try{
            //1. Validar que la venta exista
            SaleEntity sale = saleRepository.findById(request.getSaleId()).orElseThrow(
                            () -> new PaymentException("Venta no encontrada con ID" + request.getSaleId()));

            //2. Validar que la venta esté pendiente de pago
            if(sale.getPaymentStatus() == PaymentStatus.APPROVED){
                throw new PaymentException("Este venta ya fue pagada");
            }

            //3. Validar el método de pago
            if(request.getPaymentMethod() != PaymentMethod.DEBIT &&
                request.getPaymentMethod() != PaymentMethod.CREDIT){
                    throw new PaymentException("Método de pago inválido para tarjeta");
            }

            //4. Crear registro de pago
            PaymentEntity payment = createPendingPayment(sale, request);

            //5. Procesar validación con la terminal
            PaymentValidationResult validation = processWithTerminal(request, sale);

            //6. Actualizar el pago según el resultado
            if(validation.isValid()){
                completePayment(payment, sale);
            }else {
                rejectPayment(payment, validation.getErrorMessage());
            }

            //7. Guardar el pago
            PaymentEntity savedPayment = paymentRepository.save(payment);

            //8. Actualizar la venta
            sale.setPaymentStatus(savedPayment.getStatus());
            sale.setPaymentMethod(request.getPaymentMethod());
            saleRepository.save(sale);

            log.info("Pago procesado exitosamente. ID: {}, Estado: {}", savedPayment.getId(), savedPayment.getStatus());

            //9.Retornar la respuesta
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
        payment.setStatus(PaymentStatus.PEDDING);
        payment.setPaymentDate(LocalDateTime.now());
        return payment;
    }

    //Método para simular validación de tarjeta
    private PaymentValidationResult processWithTerminal(CardPaymentRequest request, SaleEntity sale){
        /*Simulación: En producción, esto se comunica con la terminal física
         o con el gateway de pago(MercadoPago, Stripe, etc.)*/

         
        log.info("Procesando con terminal simulada para la venta ID: {}", sale.getId());

        //Validar el NIP 
        if(request.getNip() == null || request.getNip().isEmpty()){
            return new PaymentValidationResult(false, "NIP requerido");
        }

        //Para simulación, NIP correcto es "1234"
        if(!"1234".equals(request.getNip())){
            return new PaymentValidationResult(false, "NIP incorrecto");
        }

        //Validar la fecha de vencimiento (simulado)
        if(!isValidExpiryDate(request.getExpiryDate())){
            return new PaymentValidationResult(false, "Tarjeta vencida o fecha inválida");
        }

        //Validar CVV (simulado: cualquier CVV de 3 dígitos es válido)
        if(request.getCvv() == null || request.getCvv().length() != 3){
            return new PaymentValidationResult(false, "CVV inválido");
        }

        //Validar número de tarjeta
        if(request.getCardNumber() == null || request.getCardNumber().length() < 16){
            return new PaymentValidationResult(false, "Número de tarjeta inválido");
        }

        //Simular saldo disponible (en producción, se consulta a la terminal)
        if(!hasSufficientBalance(request.getSaleId())){
            return new PaymentValidationResult(false, "Saldo insuficiente o límite excedido");
        }

        //Simular comunicación con la terminal
        if(!simulateTerminalCommunication()){
            return new PaymentValidationResult(false, "Error de comunicación con la terminal");
        }

        return new PaymentValidationResult(true, "Pago aprobado");
    }

    private boolean simulateTerminalCommunication(){
        //Simulación: 95% de éxito con comunicación
        return Math.random() < 0.95;
    }

    private void completePayment(PaymentEntity payment, SaleEntity sale){
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setCode(generateAuthorizationCode());
        payment.setLastfourDigits(getLastFourDigits(payment.getSale().getId()));
        //En producción, aquí se guardarían datos de la transacción de la terminal
        log.info("Paago completado con autorización: {}", payment.getCode());
    }

    private void rejectPayment(PaymentEntity payment, String errorMessage){
        payment.setStatus(PaymentStatus.REJECTED);
        payment.setErrorMessage(errorMessage);
        log.warn("Pago rechazado: {}", errorMessage);
    }

    private boolean hasSufficientBalance(Long saleId){
        //Simulación: Para fines de prueba, solo el 10% de las ventas son rechazadas
        return Math.random() > 0.1; //90% de probabilidad de aprobación
    }

    private String generateAuthorizationCode(){
        return "AUT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
                + String.format("%04d", new Random().nextInt(10000));
    }

    private String getLastFourDigits(Long saleId){
        //Simulación: Genera un número de 4 dígitos aleatorio
        //En producción, vendría de la terminal

        return String.format("%04d", new Random().nextInt(10000));
    }

    private boolean isValidExpiryDate(String expiryDate){
        //Simulación: formato MM/AA y debe ser futuro
        try{
            if(expiryDate == null || expiryDate.isEmpty()){
                return false;
            }

            String[] parts = expiryDate.split("/");
            if(parts.length != 2){
                return false;
            }

            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);

            //Validar mes
            if(month < 1 || month > 12){
                return false;
            }

            //Año completo: 2000 + year
            int fullYear = 2000 + year;
            YearMonth expiry = YearMonth.of(fullYear, month);
            return expiry.isAfter(YearMonth.now());
        } catch(Exception e) {
            log.warn("Error al validar facha de expiración: {}", expiryDate);
            return false;
        }   
    }
}
