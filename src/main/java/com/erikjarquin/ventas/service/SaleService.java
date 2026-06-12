package com.erikjarquin.ventas.service;

import com.erikjarquin.ventas.model.dto.SaleRequest;
import com.erikjarquin.ventas.model.dto.SaleResponse;

public interface SaleService {
    SaleResponse processSale(SaleRequest request);
}
