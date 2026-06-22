package com.erikjarquin.ventas.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.erikjarquin.ventas.model.enums.PaymentMethod;


public class SaleHistoryResponse {
    private Long id;
    private LocalDateTime saleDate;
    private BigDecimal total;
    private PaymentMethod paymentMethod;
    private BigDecimal cashReceived;
    private BigDecimal changeAmount;
    
    public SaleHistoryResponse(){}

    //Getter y setter de id
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    //Getter y setter de saleDate
    public LocalDateTime getSaleDate(){
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate){
        this.saleDate=saleDate;
    }

    //Getters y setter de total
    public BigDecimal getTotal(){
        return total;
    }

    public void setTotal(BigDecimal total){
        this.total=total;
    }

    //Getter y setter de paymentMethod
    public PaymentMethod getPaymentMethod(){
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod){
        this.paymentMethod=paymentMethod;
    }

    //Getter y setter de cashReceived
    public BigDecimal getCashReceived(){
        return cashReceived;
    }

    public void setCashReceived(BigDecimal cashReceived){
        this.cashReceived=cashReceived;
    }

    //Getter y setter de changeAmount
    public BigDecimal getChangeAmount(){
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount){
        this.changeAmount=changeAmount;
    }
}
