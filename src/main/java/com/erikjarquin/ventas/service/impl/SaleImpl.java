package com.erikjarquin.ventas.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.erikjarquin.ventas.model.dto.SaleItemRequest;
import com.erikjarquin.ventas.model.dto.SaleRequest;
import com.erikjarquin.ventas.model.dto.SaleResponse;
import com.erikjarquin.ventas.model.entity.ProductEntity;
import com.erikjarquin.ventas.model.entity.SaleDetailEntity;
import com.erikjarquin.ventas.model.entity.SaleEntity;
import com.erikjarquin.ventas.model.enums.PaymentMethod;
import com.erikjarquin.ventas.repository.ProductRepository;
import com.erikjarquin.ventas.repository.SaleRepository;
import com.erikjarquin.ventas.service.SaleService;

public class SaleImpl implements SaleService {
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    public SaleImpl(
        ProductRepository productRepository,
        SaleRepository saleRepository
    ){
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
    }

    @Override 
    public SaleResponse processSale(SaleRequest request){
        SaleEntity sale = new SaleEntity();
        sale.setDate(LocalDateTime.now());
        sale.setPayment(request.getPayment());

        List<SaleDetailEntity> details = new ArrayList<>();
        BigDecimal total = 0.0;

        for(SaleItemRequest item : request.getItems()){
            ProductEntity product = productRepository.findById(item.getProductId()).orElseThrow();

            if(product.getStock() < item.getQuantity()){
                throw new RuntimeException("Stock insuficiente: " + product.getName());
            }

            BigDecimal subtotal = product.getPrice() * item.getQuantity();
            total += subtotal;
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
            
            SaleDetailEntity detail = new SaleDetailEntity();
            detail.setSale(sale);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(product.getPrice());
            detail.setSubTotal(subtotal);
            details.add(detail);
        }

        sale.setTotal(total);
        if(request.getPayment() == PaymentMethod.CASH){
            sale.setCash(request.getCash());
            sale.setChange(request.getCash() - total);
        }

        sale.setDetails(details);
        SaleEntity saved = saleRepository.save(sale);
        SaleResponse response = new SaleResponse();
        response.setSaleId(saved.getId());
        response.setTotal(total);
        response.setChangeAmount(sale.getChange());
        
        return response;
    }
}
