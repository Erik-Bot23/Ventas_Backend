package com.erikjarquin.ventas.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "http://localhost:4200")
public class SaleController {
    private final SaleService service;

    public SaleController(SaleService service){
        this.service=service;
    }

    //Realizar la venta
    @PreAuthorize("hasAuthority('CREAR_VENTAS')")
    @PostMapping
    public ResponseEntity<SaleResponse> processSale(@RequestBody SaleRequest request){
        SaleResponse response = service.processSale(request);
        return ResponseEntity.ok(response);
    }

    //Ver los detalles de la venta
    @PreAuthorize("hasAuthority('VER_VENTAS')")
    @GetMapping
    public List<SaleHistoryResponse> getSales(){
        return service.getSales();
    }

    //Ver venta por ID
    @PreAuthorize("hasAuthority('VER_VENTAS')")
    @GetMapping("/{id}")
    public SaleDetailHistoryResponse getSaleById(@PathVariable Long id){
        return service.getSaleById(id);
    }  
}
