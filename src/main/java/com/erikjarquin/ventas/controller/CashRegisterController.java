package com.erikjarquin.ventas.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erikjarquin.ventas.model.dto.CashResponse;
import com.erikjarquin.ventas.model.dto.CashSummaryResponse;
import com.erikjarquin.ventas.model.dto.CloseCashRequest;
import com.erikjarquin.ventas.model.dto.OpenCashRequest;
import com.erikjarquin.ventas.service.CashRegisterService;

@PreAuthorize("hasAnyRole('ADMIN', 'CAJERO')")
@RestController
@RequestMapping("/api/cash")
@CrossOrigin(origins = "http://localhost:4200")
public class CashRegisterController {
    private final CashRegisterService service;

    public CashRegisterController(CashRegisterService service){
        this.service=service;
    }

    @GetMapping("/summary")
    public CashSummaryResponse getSummary(){
        return service.getSummary();
    }

    @PostMapping("/open")
    public CashResponse open(@RequestBody OpenCashRequest request){
        return service.open(request);
    }

    @PostMapping("/close")
    public CashResponse close(@RequestBody CloseCashRequest request){
        return service.close(request);
    }


    @GetMapping("/active")
    public CashResponse getActiveCash(){
        return service.getActiveCash();
    }

}
