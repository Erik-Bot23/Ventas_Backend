package com.erikjarquin.ventas.model.dto;

import java.util.List;

import com.erikjarquin.ventas.model.enums.PaymentMethod;

public class SaleRequest {
    private PaymentMethod paymentMethod;

    private Double cashReceived;

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
    public Double getCash(){
        return cashReceived;
    }

    public void setCash(Double cashReceived){
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
