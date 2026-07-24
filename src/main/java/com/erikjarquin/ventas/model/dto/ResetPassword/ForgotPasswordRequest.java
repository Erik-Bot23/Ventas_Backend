package com.erikjarquin.ventas.model.dto.ResetPassword;

public class ForgotPasswordRequest {
    private String email;

    //Getter y settter de email
    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email=email;
    }
}
