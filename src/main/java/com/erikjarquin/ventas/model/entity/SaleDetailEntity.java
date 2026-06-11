package com.erikjarquin.ventas.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sale_details")
public class SaleDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private SaleEntity sale;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    private Integer quantity;

    private Double unitPrice;

    private Double subtotal;

    public SaleDetailEntity(){}

    //Getter y setter de id
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    //Getter y setter de sale
    public SaleEntity getSale(){
        return sale;
    }

    public void setSale(SaleEntity sale){
        this.sale=sale;
    }

    //Getter y setter de product
    public ProductEntity getProduct(){
        return product;
    }

    public void setProduct(ProductEntity product){
        this.product=product;
    }

    //Getter y setter de quantity
    public Integer getQuantity(){
        return quantity;
    }

    public void setQuantity(Integer quantity){
        this.quantity=quantity;
    }

    //Getter y setter de unitPrice
    public Double getUnitPrice(){
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice){
        this.unitPrice=unitPrice;
    }

    //Getter y setter de subtotal
    public Double getSubTotal(){
        return subtotal;
    }

    public void setSubTotal(Double subTotal){
        this.subtotal=subTotal;
    }


}
