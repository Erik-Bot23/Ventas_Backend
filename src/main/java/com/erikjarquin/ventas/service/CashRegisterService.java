package com.erikjarquin.ventas.service;

import com.erikjarquin.ventas.model.dto.CashResponse;
import com.erikjarquin.ventas.model.dto.CloseCashRequest;
import com.erikjarquin.ventas.model.dto.OpenCashRequest;

public interface  CashRegisterService {
    CashResponse open(OpenCashRequest request);

    CashResponse close(CloseCashRequest request);

    CashResponse getActiveCash();
}
