package com.erikjarquin.ventas.service;


import java.util.List;

import com.erikjarquin.ventas.model.dto.SaleDetailHistoryResponse;
import com.erikjarquin.ventas.model.dto.SaleHistoryResponse;
import com.erikjarquin.ventas.model.dto.SaleRequest;
import com.erikjarquin.ventas.model.dto.SaleResponse;

public interface SaleService {
    SaleResponse processSale(SaleRequest request);

    List<SaleHistoryResponse> getSales();
    
    SaleDetailHistoryResponse getSaleById(Long saleId);
}
