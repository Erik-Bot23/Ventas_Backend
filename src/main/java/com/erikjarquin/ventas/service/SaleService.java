package com.erikjarquin.ventas.service;


import java.util.List;

import com.erikjarquin.ventas.model.dto.Sale.SaleDetailHistoryResponse;
import com.erikjarquin.ventas.model.dto.Sale.SaleHistoryResponse;
import com.erikjarquin.ventas.model.dto.Sale.SaleRequest;
import com.erikjarquin.ventas.model.dto.Sale.SaleResponse;

public interface SaleService {
    SaleResponse processSale(SaleRequest request);

    List<SaleHistoryResponse> getSales();
    
    SaleDetailHistoryResponse getSaleById(Long saleId);
}
