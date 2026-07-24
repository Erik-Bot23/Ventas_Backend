package com.erikjarquin.ventas.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erikjarquin.ventas.model.dto.Sale.SaleDetailHistoryResponse;
import com.erikjarquin.ventas.model.dto.Sale.SaleHistoryResponse;
import com.erikjarquin.ventas.model.dto.Sale.SaleRequest;
import com.erikjarquin.ventas.model.dto.Sale.SaleResponse;
import com.erikjarquin.ventas.service.SaleService;

@PreAuthorize("hasAnyRole('ADMIN', 'CAJERO')")
@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "http://localhost:4200")
public class SaleController {
    private final SaleService service;

    public SaleController(SaleService service){
        this.service=service;
    }

    @PostMapping
    public SaleResponse processSale(@RequestBody SaleRequest request){
        return service.processSale(request);
    }

    @GetMapping
    public List<SaleHistoryResponse> getSales(){
        return service.getSales();
    }

    @GetMapping("/{id}")
    public SaleDetailHistoryResponse getSaleById(@PathVariable Long id){
        return service.getSaleById(id);
    }
    
}
