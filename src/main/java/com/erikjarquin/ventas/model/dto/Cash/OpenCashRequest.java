package com.erikjarquin.ventas.model.dto.Cash;

import java.math.BigDecimal;

//DTO de respuesta de caja abierta
public class OpenCashRequest {
    private BigDecimal openingAmount;

    //Constructor vacío
    public OpenCashRequest(){}

    //Getter y setter de openingAmount
    public BigDecimal getOpeningAmount(){
        return openingAmount;
    }

    public void setOpeningAmount(BigDecimal openingAmount){
        this.openingAmount = openingAmount;
    }
}
