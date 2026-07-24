package com.erikjarquin.ventas.model.dto.Sale;

import java.math.BigDecimal;

public class SaleResponse {
    private Long saleId;

    private BigDecimal total;

    private BigDecimal cashReceived;

    private BigDecimal changeAmount;

    public SaleResponse(){}

    //Getter y setter de Id
    public Long getSaleId(){
        return saleId;
    }

    public void setSaleId(Long saleId){
        this.saleId=saleId;
    }

    //Getter y setter de total
    public BigDecimal getTotal(){
        return total;
    }

    public void setTotal(BigDecimal total){
        this.total=total;
    }

    //Getter y setter changeAmount
    public BigDecimal getChangeAmount(){
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount){
        this.changeAmount=changeAmount;
    }

    //Getter y setter de cashReceived
    public BigDecimal getCashReceived(){
        return cashReceived;
    }

    public void setCashReceived(BigDecimal cashReceived){
        this.cashReceived=cashReceived;
    }
}
