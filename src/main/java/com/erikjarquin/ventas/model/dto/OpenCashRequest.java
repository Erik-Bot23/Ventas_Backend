package com.erikjarquin.ventas.model.dto;

import java.math.BigDecimal;

public class OpenCashRequest {
    private BigDecimal openingAmount;

    public OpenCashRequest(){}

    //Getter y setter de openingAmount
    public BigDecimal getOpeningAmount(){
        return openingAmount;
    }

    public void setOpeningAmount(BigDecimal openingAmount){
        this.openingAmount = openingAmount;
    }
}
