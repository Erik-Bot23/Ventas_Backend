package com.erikjarquin.ventas.model.enums;

public enum PaymentStatus {
    PENDING("Pendiente"),
    APPROVED("Aprobado"),
    REJECTED("Rechazado");

    private String description;

    PaymentStatus(String description){
        this.description=description;
    }

    public String getDescription(){
        return description;
    }
}
