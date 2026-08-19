package com.erikjarquin.ventas.model.dto.Sale;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.erikjarquin.ventas.model.enums.PaymentMethod;
import com.erikjarquin.ventas.model.enums.PaymentStatus;

public class SaleDetailHistoryResponse {
    private Long saleId;
    private LocalDateTime saleDate;
    private BigDecimal total;
    private PaymentMethod paymentMethod;
    private List<SaleDetailResponse> items;
    private PaymentStatus paymentStatus;

    //Getter y setter de saleId
    public Long getSaleId(){
        return saleId;
    }

    public void setSaleId(Long saleId){
        this.saleId = saleId;
    }

    //Getter y setter de saleDate
    public LocalDateTime getSaleDate(){
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate){
        this.saleDate = saleDate;
    }

    //Getter y setter de total
    public BigDecimal getTotal(){
        return total;
    }

    public void setTotal(BigDecimal total){
        this.total = total;
    }

    //Getter y setter paymentMethod
    public PaymentMethod getPaymentMethod(){
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod){
        this.paymentMethod = paymentMethod;
    }

    //Getter y setter items
    public List<SaleDetailResponse> getItems(){
        return items;
    }

    public void setItems(List<SaleDetailResponse> items){
        this.items = items;
    }

    //Getter y setter
    public PaymentStatus getPaymentStatus(){
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus){
        this.paymentStatus=paymentStatus;
    }
    
}
