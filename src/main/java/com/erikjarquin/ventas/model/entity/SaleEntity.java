package com.erikjarquin.ventas.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    private LocalDateTime saleDate;

    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private BigDecimal cashReceived;

    private BigDecimal changeAmount;

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
    public LocalDateTime getDate(){
        return saleDate;
    }

    public void setDate(LocalDateTime saleDate){
        this.saleDate=saleDate;
    }

    //Getter y setter de total
    public BigDecimal getTotal(){
        return total;
    }

    public void setTotal(BigDecimal total){
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
    public BigDecimal getCash(){
        return cashReceived;
    }

    public void setCash(BigDecimal cashReceived){
        this.cashReceived=cashReceived;
    }

    //Getter y setter de changeAmount
    public BigDecimal getChange(){
        return changeAmount;
    }

    public void setChange(BigDecimal changeAmount){
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
