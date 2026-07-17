package com.erikjarquin.ventas.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erikjarquin.ventas.mapper.SaleMapper;
import com.erikjarquin.ventas.model.dto.SaleDetailHistoryResponse;
import com.erikjarquin.ventas.model.dto.SaleHistoryResponse;
import com.erikjarquin.ventas.model.dto.SaleItemRequest;
import com.erikjarquin.ventas.model.dto.SaleRequest;
import com.erikjarquin.ventas.model.dto.SaleResponse;
import com.erikjarquin.ventas.model.entity.CashRegisterEntity;
import com.erikjarquin.ventas.model.entity.ProductEntity;
import com.erikjarquin.ventas.model.entity.SaleDetailEntity;
import com.erikjarquin.ventas.model.entity.SaleEntity;
import com.erikjarquin.ventas.model.enums.PaymentMethod;
import com.erikjarquin.ventas.repository.CashRegisterRepository;
import com.erikjarquin.ventas.repository.ProductRepository;
import com.erikjarquin.ventas.repository.SaleRepository;
import com.erikjarquin.ventas.service.SaleService;

@Service
@Transactional
public class SaleImpl implements SaleService {
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final CashRegisterRepository cashRepository;
    private final SaleMapper mapper;

    public SaleImpl(
        ProductRepository productRepository,
        SaleRepository saleRepository,
        CashRegisterRepository cashRepository,
        SaleMapper mapper
    ){
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.cashRepository = cashRepository;
        this.mapper = mapper;
    }

    @Override 
    public SaleResponse processSale(SaleRequest request){
        CashRegisterEntity cash = cashRepository.findByActiveTrue().orElseThrow(() -> 
            new RuntimeException("No existe una caja abierta"));

        //Validar que haya items
        if(request.getItems() == null || request.getItems().isEmpty()){
            throw new RuntimeException("La venta no tiene productos");
        }

        SaleEntity sale = new SaleEntity();
        sale.setSaleDate(LocalDateTime.now());
        sale.setPaymentMethod(request.getPaymentMethod());
        sale.setCashRegister(cash);

        List<SaleDetailEntity> details = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for(SaleItemRequest item : request.getItems()){
            ProductEntity product = productRepository.findById(item.getProductId()).orElseThrow(() -> 
            new RuntimeException("Producto no encontrado: " + item.getProductId()));

            //Validar que haya stock
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

        if(request.getPaymentMethod() == PaymentMethod.CASH){
            if(request.getCashReceived() == null || request.getCashReceived().compareTo(total) < 0){
                throw new RuntimeException("Pago insuficiente");
            }
        }

        sale.setTotal(total);
        BigDecimal change = BigDecimal.ZERO;

        if(request.getPaymentMethod() == PaymentMethod.CASH){
            change = request.getCashReceived().subtract(total);
            sale.setCashReceived(request.getCashReceived());
            sale.setChangeAmount(change);
        }

        sale.setDetails(details);
        SaleEntity saved = saleRepository.save(sale);
        
        return mapper.toResponse(saved);
    }

    @Override
    public List<SaleHistoryResponse> getSales(){
        return saleRepository.findAll().stream().map(mapper::toHistoryResponse).toList();
    }

    @Override
    public SaleDetailHistoryResponse getSaleById(Long saleId){
        SaleEntity sale = saleRepository.findById(saleId).orElseThrow();

        return mapper.toDetailResponse(sale);
    }
}
