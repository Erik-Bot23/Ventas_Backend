package com.erikjarquin.ventas.model.enums;

public enum PaymentStatus {
    PENDING("Pendiente"),
    PROCESSING("Procesando"), //Enviado a terminal
    APPROVED("Aprobado"),
    REJECTED("Rechazado"),
    REVERSED("Reversado"), //Cancelado después de aprobar
    REVERSAL_PENDING("Reversa Pendiente"), //Reversa en proceso
    REVERSAL_FAILED("Reversa Fallida"); //No se pudo reversar

    private String description;

    PaymentStatus(String description){
        this.description=description;
    }

    public String getDescription(){
        return description;
    }
}
