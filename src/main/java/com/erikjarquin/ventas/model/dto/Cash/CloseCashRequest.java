package com.erikjarquin.ventas.model.dto.Cash;

import java.math.BigDecimal;

public class CloseCashRequest {
    private BigDecimal closingAmount;

    public CloseCashRequest(){}

    //Getter y setter de closingAmount
    public BigDecimal getClosingAmount(){
        return closingAmount;
    }

    public void setClosingAmount(BigDecimal closingAmount){
        this.closingAmount=closingAmount;
    }
}
