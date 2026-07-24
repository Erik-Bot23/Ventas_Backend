package com.erikjarquin.ventas.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.erikjarquin.ventas.model.dto.Sale.SaleDetailHistoryResponse;
import com.erikjarquin.ventas.model.dto.Sale.SaleDetailResponse;
import com.erikjarquin.ventas.model.dto.Sale.SaleHistoryResponse;
import com.erikjarquin.ventas.model.dto.Sale.SaleResponse;
import com.erikjarquin.ventas.model.entity.SaleDetailEntity;
import com.erikjarquin.ventas.model.entity.SaleEntity;

@Component
public class SaleMapper {
    public SaleResponse toResponse(SaleEntity sale){
        SaleResponse response = new SaleResponse();

        response.setSaleId(sale.getId());
        response.setTotal(sale.getTotal());
        response.setCashReceived(sale.getCashReceived());
        response.setChangeAmount(sale.getChangeAmount());

        return response;
    }

    public SaleHistoryResponse toHistoryResponse(SaleEntity sale){
        SaleHistoryResponse response = new SaleHistoryResponse();

        response.setId(sale.getId());
        response.setSaleDate(sale.getSaleDate());
        response.setTotal(sale.getTotal());
        response.setPaymentMethod(sale.getPaymentMethod());
        response.setCashReceived(sale.getCashReceived());
        response.setChangeAmount(sale.getChangeAmount());

        return response;
    }

    public SaleDetailHistoryResponse toDetailResponse(SaleEntity sale){
        SaleDetailHistoryResponse response = new SaleDetailHistoryResponse();

        response.setSaleId(sale.getId());
        response.setSaleDate(sale.getSaleDate());
        response.setTotal(sale.getTotal());
        response.setPaymentMethod(sale.getPaymentMethod());
        List<SaleDetailResponse> items = sale.getDetails() == null ? List.of() : sale.getDetails().stream().map(this::toDetailItem).toList();
        response.setItems(items);

        return response;
        
    }

    private SaleDetailResponse toDetailItem(SaleDetailEntity detail){
        SaleDetailResponse item = new SaleDetailResponse();

        item.setProduct(detail.getProduct().getName());
        item.setQuantity(detail.getQuantity());
        item.setUnitPrice(detail.getUnitPrice());
        item.setSubtotal(detail.getSubTotal());

        return item;
    }
}
