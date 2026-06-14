package com.erikjarquin.ventas.model.dto;

import java.math.BigDecimal;
import java.util.List;

import com.erikjarquin.ventas.model.enums.PaymentMethod;

public class SaleRequest {
    private PaymentMethod paymentMethod;

    private BigDecimal cashReceived;

    private List<SaleItemRequest> items;

    public SaleRequest(){}

    //Getter y setter de paymentMethod
    public PaymentMethod getPayment(){
        return paymentMethod;
    }

    public void setPayment(PaymentMethod paymentMethod){
        this.paymentMethod=paymentMethod;
    }

    //Getter y setter de paymentMethod
    public BigDecimal getCash(){
        return cashReceived;
    }

    public void setCash(BigDecimal cashReceived){
        this.cashReceived=cashReceived;
    }

    //Getter y setter de paymentMethod
    public List<SaleItemRequest> getItems(){
        return items;
    }

    public void setItems(List<SaleItemRequest> items){
        this.items=items;
    }
}
