package com.erikjarquin.ventas.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erikjarquin.ventas.model.dto.SaleRequest;
import com.erikjarquin.ventas.model.dto.SaleResponse;
import com.erikjarquin.ventas.service.SaleService;

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
}
