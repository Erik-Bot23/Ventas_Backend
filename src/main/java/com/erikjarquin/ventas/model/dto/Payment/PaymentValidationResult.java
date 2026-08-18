package com.erikjarquin.ventas.model.dto.Payment;

public class PaymentValidationResult {
    private boolean valid;
    private String errorMessage;  
    
    //Getter y setter de valid
    public boolean getValid(){
        return valid;
    }

    public void setValid(boolean valid){
        this.valid=valid;
    }

    //Getter y setter de errorMessage
    public String getErrorMessage(){
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage){
        this.errorMessage=errorMessage;
    }
}
