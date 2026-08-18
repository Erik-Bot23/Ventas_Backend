package com.erikjarquin.ventas.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.erikjarquin.ventas.model.enums.PaymentMethod;
import com.erikjarquin.ventas.model.enums.PaymentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private SaleEntity sale;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; //DEBIT O CREDIT

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;//PEDDING, APPROVED, REJECTED

    private BigDecimal amount;

    private String authorizationCode; //Código de autorización simulado

    private LocalDateTime paymentDate;

    private String lastFourDigits; //Últimos cuatro dígitos de la tarjeta (por seguridad)

    private String errorMesaage; // Si fue rechazado, razón

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

    //Getter y setter de paymentMethod
    public PaymentMethod getPaymentMethod(){
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod){
        this.paymentMethod = paymentMethod;
    }

    //Getter y setter de status
    public PaymentStatus getStatus(){
        return status;
    }

    public void setStatus(PaymentStatus status){
        this.status=status;
    }

    //Getter y setter de amount
    public BigDecimal getAmount(){
        return amount;
    }

    public void setAmount(BigDecimal amount){
        this.amount=amount;
    }

    //Getter y setter de codigo
    public String getCode(){
        return authorizationCode;
    }

    public void setCode(String authorizationCode){
        this.authorizationCode=authorizationCode;
    }

    //Getter y setter de paymentDay
    public LocalDateTime getPaymentDate(){
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate){
        this.paymentDate=paymentDate;
    }

    //Getter y setter de lastFourDigits
    public String getLastFourDigits(){
        return lastFourDigits;
    }

    public void setLastfourDigits(String lastFourDigits){
        this.lastFourDigits=lastFourDigits;
    }

    //Getter y setter de errorMessage
    public String getErrorMessage(){
        return errorMesaage;
    }

    public void setErrorMessage(String errorMessage){
        this.errorMesaage=errorMessage;
    }
}
