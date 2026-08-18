package com.erikjarquin.ventas.service.impl;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erikjarquin.ventas.mapper.PaymentMapper;
import com.erikjarquin.ventas.model.dto.Payment.CardPaymentRequest;
import com.erikjarquin.ventas.model.dto.Payment.CardPaymentResponse;
import com.erikjarquin.ventas.model.entity.PaymentEntity;
import com.erikjarquin.ventas.model.entity.SaleEntity;
import com.erikjarquin.ventas.model.enums.PaymentStatus;
import com.erikjarquin.ventas.repository.PaymentRepository;
import com.erikjarquin.ventas.repository.SaleRepository;
import com.erikjarquin.ventas.service.PaymentService;

@Service
@Transactional
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
        //1. Validar que la venta exista
        SaleEntity sale = saleRepository.findById(request.getSaleId()).orElseThrow(
                            () -> new RuntimeException("Venta no encontrada"));
        
        //2. Crear registro de pago pendiente
        PaymentEntity payment = new PaymentEntity();
        payment.setSale(sale);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(sale.getTotal());
        payment.setStatus(PaymentStatus.PEDDING);
        payment.setPaymentDate(LocalDateTime.now());

        //3. Simular validación de tarjeta
        PaymentValidationResult validation = validateCard(request);

        if(validation.isValid()){
            payment.setStatus(PaymentStatus.APPROVED);
            payment.setCode(generateAuthorizationCode());
            payment.setLastfourDigits(getLastFourDigits(request.getCardNumber()));
        } else {
            payment.setStatus(PaymentStatus.REJECTED);
            payment.setErrorMessage(validation.getErrorMessage());
        }

        //4. Guardar el pago
        PaymentEntity savedPayment = paymentRepository.save(payment);

        //5. Actualizar la venta
        sale.setPaymentStatus(savedPayment.getStatus());
        saleRepository.save(sale);

        //6. Retornar la respuesta
        return paymentMapper.toCardPaymentResponse(savedPayment);
    }

    //Método para simular validación de tarjeta
    private PaymentValidationResult validateCard(CardPaymentRequest request){
        /*Simulación: En producción, esto se comunica con la terminal física
         o con el gateway de pago(MercadoPago, Stripe, etc.)
        */

        //Validar el NIP (simulado: NIP correcto es "1234")
        if(!"1234".equals(request.getNip())){
            return new PaymentValidationResult(false, "NIP incorrecto");
        }

        //Validar la fecha de vencimiento (simulado)
        if(!isValidExpiryDate(request.getExpiryDate())){
            return new PaymentValidationResult(false, "Tarjeta vencida");
        }

        //Validar CVV (simulado: cualquier CVV de 3 dígitos es válido)
        if(request.getCvv() == null || request.getCvv().length() != 3){
            return new PaymentValidationResult(false, "CVV inválido");
        }

        //Simular saldo disponible (en producción, se consulta a la terminal)
        if(!hasSufficientBalance(request.getSaleId())){
            return new PaymentValidationResult(false, "Saldo insuficiente");
        }

        return new PaymentValidationResult(true, "Pago aprobado");
    }

    private boolean hasSufficientBalance(Long saleId){
        //Simulación: Para fines de prueba, solo el 10% de las ventas son rechazadas
        return Math.random() > 0.1; //90% de probabilidad de aprobación
    }

    private String generateAuthorizationCode(){
        return "AUT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
                + String.format("%04d", new Random().nextInt(10000));
    }

    private String getLastFourDigits(String cardNumber){
        if(cardNumber == null || cardNumber.length() <4){
            return "****";
        }

        return cardNumber.substring(cardNumber.length() - 4);
    }

    private boolean isValidExpiryDate(String expiryDate){
        //Simulación: formato MM/AA y debe ser futuro
        try{
            String[] parts = expiryDate.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);
            //Año completo: 2000 + year
            YearMonth expiry = YearMonth.of(2000 + year, month);
            return expiry.isAfter(YearMonth.now());
        } catch(Exception e) {
            return false;
        }   
    }
}
