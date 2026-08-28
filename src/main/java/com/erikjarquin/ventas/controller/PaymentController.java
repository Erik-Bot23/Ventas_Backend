package com.erikjarquin.ventas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erikjarquin.ventas.model.dto.Payment.CardPaymentRequest;
import com.erikjarquin.ventas.model.dto.Payment.CardPaymentResponse;
import com.erikjarquin.ventas.model.enums.PaymentStatus;
import com.erikjarquin.ventas.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService=paymentService;
    }

    @PreAuthorize("hasAuthority('PROCESAR_PAGOS')")
    @PostMapping("/card")
    public ResponseEntity<CardPaymentResponse> processCardPayment(@RequestBody CardPaymentRequest request){
        try{
            CardPaymentResponse response = paymentService.processCardPayment(request);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            //Manejo de errores
            CardPaymentResponse errorResponse = new CardPaymentResponse();
            errorResponse.setStatus(PaymentStatus.REJECTED);
            errorResponse.setMessage("Error al procesar el pago" + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    //Consultar estado por transactionId
    @PreAuthorize("hasAuthority('PROCESAR_PAGOS')")
    @GetMapping("/status/{transactionId}")
    public ResponseEntity<CardPaymentResponse> getPaymentStatus(@PathVariable String transactionId){
        CardPaymentResponse response = paymentService.getPaymentStatus(transactionId);

        return ResponseEntity.ok(response);
    }

    //Reintentar pago
    @PreAuthorize("hasAuthority('PROCESAR_PAGOS')")
    @PostMapping("retry/{paymentId}")
    public ResponseEntity<CardPaymentResponse> retryPayment(@PathVariable Long paymentId){
        CardPaymentResponse response = paymentService.retryPayment(paymentId);
        return ResponseEntity.ok(response);
    }
}
