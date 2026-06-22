package com.erikjarquin.ventas.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CashResponse {
    private Long id;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private BigDecimal openingAmount;
    private BigDecimal closingAmount;
    private Boolean active;

    public CashResponse(){}

    //Getter y setter de id
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    //Getter y setter de openedAt
    public LocalDateTime getOpenedAt(){
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt){
        this.openedAt=openedAt;
    }

    //Getter y setter de closedAt
    public LocalDateTime getClosedAt(){
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt){
        this.closedAt=closedAt;
    }

    //Getter y setter de openingAmount
    public BigDecimal getOpeningAmount(){
        return openingAmount;
    }

    public void setOpeningAmount(BigDecimal openingAmount){
        this.openingAmount=openingAmount;
    }

    //Getter y setter de closingAmount
    public BigDecimal getClosingAmount(){
        return closingAmount;
    }

    public void setClosingAmount(BigDecimal closingAmount){
        this.closingAmount=closingAmount;
    }

    //Getter y setter de active
    public Boolean getActive(){
        return active;
    }

    public void setActive(Boolean active){
        this.active=active;
    }
    
}
