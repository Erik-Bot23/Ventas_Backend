package com.erikjarquin.ventas.service;

import com.erikjarquin.ventas.model.dto.Payment.CardPaymentRequest;
import com.erikjarquin.ventas.model.dto.Payment.CardPaymentResponse;

public interface PaymentService {
    CardPaymentResponse processCardPayment(CardPaymentRequest request);
} 