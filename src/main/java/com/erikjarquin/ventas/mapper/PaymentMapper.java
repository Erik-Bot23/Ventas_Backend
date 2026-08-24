package com.erikjarquin.ventas.mapper;

import org.springframework.stereotype.Component;

import com.erikjarquin.ventas.model.dto.Payment.CardPaymentResponse;
import com.erikjarquin.ventas.model.entity.PaymentEntity;
import com.erikjarquin.ventas.model.enums.PaymentStatus;

@Component
public class PaymentMapper {
    public CardPaymentResponse toCardPaymentResponse(PaymentEntity payment){
        CardPaymentResponse response = new CardPaymentResponse();
        response.setPaymentId(payment.getId());
        response.setSaleId(payment.getSale().getId());
        response.setStatus(payment.getStatus());
        response.setAuthorizationCode(payment.getAuthorizationCode());
        response.setAmount(payment.getAmount());
        response.setPaymentDate(payment.getPaymentDate());

        //Mensaje según el estado
        if(payment.getStatus() == PaymentStatus.APPROVED){
            response.setMessage("Pago aprobado con éxito");
        } else if(payment.getStatus() == PaymentStatus.REJECTED){
            response.setMessage(payment.getErrorMessage());
        } else{
            response.setMessage("Pago en proceso");
        }

        return response;
    }
}
