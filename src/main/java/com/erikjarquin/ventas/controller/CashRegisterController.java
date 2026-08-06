package com.erikjarquin.ventas.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erikjarquin.ventas.model.dto.Cash.CashResponse;
import com.erikjarquin.ventas.model.dto.Cash.CashSummaryResponse;
import com.erikjarquin.ventas.model.dto.Cash.CloseCashRequest;
import com.erikjarquin.ventas.model.dto.Cash.OpenCashRequest;
import com.erikjarquin.ventas.service.CashRegisterService;

@RestController
@RequestMapping("/api/cash")
@CrossOrigin(origins = "http://localhost:4200")
public class CashRegisterController {
    private final CashRegisterService service;

    public CashRegisterController(CashRegisterService service){
        this.service=service;
    }

    @PreAuthorize("hasAuthority('VER_CORTE_CAJA')")
    @GetMapping("/summary")
    public CashSummaryResponse getSummary(){
        return service.getSummary();
    }

    @PreAuthorize("hasAuthority('ABRIR_CAJA')")
    @PostMapping("/open")
    public CashResponse open(@RequestBody OpenCashRequest request){
        return service.open(request);
    }

    @PreAuthorize("hasAuthority('CERRAR_CAJA')")
    @PostMapping("/close")
    public CashResponse close(@RequestBody CloseCashRequest request){
        return service.close(request);
    }

    @PreAuthorize("hasAuthority('VER_CAJA_ACTIVA')")
    @GetMapping("/active")
    public CashResponse getActiveCash(){
        return service.getActiveCash();
    }

}
