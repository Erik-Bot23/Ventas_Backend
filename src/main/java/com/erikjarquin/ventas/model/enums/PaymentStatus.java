package com.erikjarquin.ventas.model.enums;

public enum PaymentStatus {
    PEDDING("Pendiente"),
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
