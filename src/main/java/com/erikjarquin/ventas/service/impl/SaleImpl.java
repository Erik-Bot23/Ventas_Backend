package com.erikjarquin.ventas.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erikjarquin.ventas.mapper.SaleMapper;
import com.erikjarquin.ventas.model.dto.Sale.SaleDetailHistoryResponse;
import com.erikjarquin.ventas.model.dto.Sale.SaleHistoryResponse;
import com.erikjarquin.ventas.model.dto.Sale.SaleItemRequest;
import com.erikjarquin.ventas.model.dto.Sale.SaleRequest;
import com.erikjarquin.ventas.model.dto.Sale.SaleResponse;
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
        
        /* 1. VALIDAR SOLICITUD */
        if(request == null){
            throw new RuntimeException("La solicitud de venta es obligatoria");
        }

        if(request.getPaymentMethod() == null){
            throw new RuntimeException("Debe seleccionar un método de pago");
        }
        
        /* 2. VALIDAR CAJA ABIERTA */
        CashRegisterEntity cash = cashRepository.findByActiveTrue().orElseThrow(() -> 
            new RuntimeException("No existe una caja abierta"));

        /* 3. VALIDAR PRODUCTOS*/
        if(request.getItems() == null || request.getItems().isEmpty()){
            throw new RuntimeException("La venta no tiene productos");
        }

        /* 4. CREAR VENTA */
        SaleEntity sale = new SaleEntity();
        sale.setSaleDate(LocalDateTime.now());
        sale.setPaymentMethod(request.getPaymentMethod());
        sale.setCashRegister(cash);

        List<SaleDetailEntity> details = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        /* 5. PROCESAR PRODUCTOS*/
        for(SaleItemRequest item : request.getItems()){
            if(item == null){
                throw new RuntimeException("La venta contiene un producto inválido");
            }

            if(item.getProductId() == null){
                throw new RuntimeException("El producto es obligatorio");
            }

            if(item.getQuantity() == null || item.getQuantity() <= 0){
                throw new RuntimeException("La cantidad del producto debe ser mayor a 0");
            }
            
            ProductEntity product = productRepository.findById(item.getProductId()).orElseThrow(() -> 
            new RuntimeException("Producto no encontrado: " + item.getProductId()));

            /* 6. Validar que haya stock*/
            if(product.getStock() < item.getQuantity()){
                throw new RuntimeException("Stock insuficiente: " + product.getName());
            }

            /* 7. CALCULAR SUBTOTAL*/
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(subtotal);

            /* 8. DESCONTAR STOCK*/
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
            
            /* 9. CREAR DETALLE*/
            SaleDetailEntity detail = new SaleDetailEntity();
            detail.setSale(sale);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(product.getPrice());
            detail.setSubTotal(subtotal);
            details.add(detail);
        }

        /* 10. VALIDAR PAGO EN EFECTIVO*/
        if(request.getPaymentMethod() == PaymentMethod.CASH){
            if(request.getCashReceived() == null){
                throw new RuntimeException("Debe indicar el efectivo recibido");
            }

            if(request.getCashReceived().compareTo(BigDecimal.ZERO) <= 0){
                throw new RuntimeException("El efectivo recibido debe ser mayor a cero");
            }

            if(request.getCashReceived().compareTo(total) < 0){
                throw new RuntimeException("Pago insuficiente");
            }
        }

        /* 11. GUARDAR TOTAL*/
        sale.setTotal(total);
        BigDecimal change = BigDecimal.ZERO;


        /* 12. CALCULAR CAMBIO*/
        if(request.getPaymentMethod() == PaymentMethod.CASH){
            change = request.getCashReceived().subtract(total);
            sale.setCashReceived(request.getCashReceived());
            sale.setChangeAmount(change);
        } else{
            sale.setCashReceived(null);
            sale.setChangeAmount(null);
        }

        /* 13. ASOCIAR DETALLES*/
        sale.setDetails(details);

        /* 14. GUARDAR VENTA*/
        SaleEntity saved = saleRepository.save(sale);
        
        /* 15. RESULTADO*/
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
