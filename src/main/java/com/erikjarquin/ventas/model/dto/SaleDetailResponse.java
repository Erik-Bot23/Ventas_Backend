package com.erikjarquin.ventas.model.dto;

import java.math.BigDecimal;

public class SaleDetailResponse {
    private String product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    //Getter y setter de product
    public String getProduct(){
        return product;
    }

    public void setProduct(String product){
        this.product = product;
    }

    //Getter y setter de queantity
    public Integer getQuantity(){
        return quantity;
    }

    public void setQuantity(Integer quantity){
        this.quantity = quantity;
    }

    //Getter y setter de unitPrice
    public BigDecimal getUnitPrice(){
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice){
        this.unitPrice = unitPrice;
    }

    //Getter y setter de subtotal
    public BigDecimal getSubtotal(){
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal){
        this.subtotal = subtotal;
    }
    

}
