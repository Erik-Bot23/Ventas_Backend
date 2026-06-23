package com.erikjarquin.ventas.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.erikjarquin.ventas.model.enums.PaymentMethod;

public class SaleDetailHistoryResponse {
    private Long saleId;
    private LocalDateTime saleDate;
    private BigDecimal total;
    private PaymentMethod paymentMethod;
    private List<SaleDetailResponse> items;

    
}
