package com.erikjarquin.ventas.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erikjarquin.ventas.model.dto.SaleHistoryResponse;
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

@Service
@Transactional
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
        sale.setPaymentMethod(request.getPaymentMethod());

        List<SaleDetailEntity> details = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for(SaleItemRequest item : request.getItems()){
            ProductEntity product = productRepository.findById(item.getProductId()).orElseThrow();

            if(product.getStock() < item.getQuantity()){
                throw new RuntimeException("Stock insuficiente: " + product.getName());
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(subtotal);
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

        if(request.getPaymentMethod() == PaymentMethod.CASH && request.getCashReceived().compareTo(total) < 0){
            throw new RuntimeException("Pago insuficiente");
        }
        sale.setTotal(total);
        BigDecimal change = request.getCashReceived().subtract(total);
        if(request.getPaymentMethod() == PaymentMethod.CASH){
            sale.setCashReceived(request.getCashReceived());
            sale.setChangeAmount(change);
        }

        sale.setDetails(details);
        SaleEntity saved = saleRepository.save(sale);
        SaleResponse response = new SaleResponse();
        response.setSaleId(saved.getId());
        response.setTotal(total);
        response.setChangeAmount(sale.getChangeAmount());
        
        return response;
    }

    @Override
    public List<SaleHistoryResponse> getSales(){
        return saleRepository.findAll().stream().map(sale -> {
            SaleHistoryResponse dto = new SaleHistoryResponse();

            dto.setId(sale.getId());
            dto.setSaleDate(sale.getDate());
            dto.setTotal(sale.getTotal());
            dto.setPaymentMethod(sale.getPaymentMethod());
            dto.setCashReceived(sale.getCashReceived());
            dto.setChangeAmount(sale.getChangeAmount());

            return dto;
        }).toList();
    }
}
