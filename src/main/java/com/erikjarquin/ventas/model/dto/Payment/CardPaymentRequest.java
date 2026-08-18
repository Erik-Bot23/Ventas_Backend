package com.erikjarquin.ventas.model.dto.Payment;

import com.erikjarquin.ventas.model.enums.PaymentMethod;

public class CardPaymentRequest {
    private Long saleId; //ID de la venta existente
    private PaymentMethod paymentMethod; //DEBIT o CREDIT
    private String cardNumber; //Solo para simulación, en producción no se guarda
    private String expiryDate; //MM/AA
    private String cvv; //Solo para simulación
    private String nip; //NIP del usuario (simulado)

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

    //Getter y setter de cardNumber
    public String getCardNumber(){
        return cardNumber;
    }

    public void setCardNumber(String cardNumber){
        this.cardNumber=cardNumber;
    }

    //Getter y setter expiryDate
    public String getExpiryDate(){
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate){
        this.expiryDate=expiryDate;
    }

    //Getter y setter de cvv
    public String getCvv(){
        return cvv;
    }

    public void setCvv(String cvv){
        this.cvv=cvv;
    }

    //Getter y setter de nip del usuario
    public String getNip(){
        return nip;
    }

    public void setNip(String nip){
        this.nip=nip;
    }

}
