package com.erikjarquin.ventas.model.dto.Payment;

import com.erikjarquin.ventas.model.enums.PaymentMethod;

public class CardPaymentRequest {
    private Long saleId; //ID de la venta existente
    private PaymentMethod paymentMethod; //DEBIT o CREDIT

    //Getter y setter de saleId
    public Long getSaleId(){
        return saleId;
    }

    public void setSaleId(Long saleId){
        this.saleId=saleId;
    }

    //Getter y setter de paymentMethod
    public PaymentMethod getPaymentMethod(){
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod){
        this.paymentMethod=paymentMethod;
    }

}
