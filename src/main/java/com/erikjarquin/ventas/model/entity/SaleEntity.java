package com.erikjarquin.ventas.model.entity;

import java.time.LocalDate;
import java.util.List;

import com.erikjarquin.ventas.model.enums.PaymentMethod;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "sales")
public class SaleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate saleDate;

    private Double total;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private Double cashReceived;

    private Double changeAmount;

    @OneToMany(
        mappedBy = "sale",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<SaleDetailEntity> details;

    public SaleEntity(){}

    //Getter y setter de id
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    //Getter y setter de saleDate
    public LocalDate getDate(){
        return saleDate;
    }

    public void setDate(LocalDate saleDate){
        this.saleDate=saleDate;
    }

    //Getter y setter de total
    public Double getTotal(){
        return total;
    }

    public void setTotal(Double total){
        this.total=total;
    }

    //Getter y setter de paymentMethod
    public PaymentMethod getPayment(){
        return paymentMethod;
    }

    public void setPayment(PaymentMethod paymentMethod){
        this.paymentMethod=paymentMethod;
    }

    //Getter y setter de cashReceived
    public Double getCash(){
        return cashReceived;
    }

    public void setCash(Double cashReceived){
        this.cashReceived=cashReceived;
    }

    //Getter y setter de changeAmount
    public Double getChange(){
        return changeAmount;
    }

    public void setChange(Double changeAmount){
        this.changeAmount=changeAmount;
    }

    //Getter y setter de details
    public List<SaleDetailEntity> getDetails(){
        return details;
    }

    public void setDetails(List<SaleDetailEntity> details){
        this.details=details;
    }

}
