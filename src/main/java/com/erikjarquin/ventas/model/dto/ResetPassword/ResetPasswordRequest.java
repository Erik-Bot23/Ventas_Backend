package com.erikjarquin.ventas.model.dto.ResetPassword;

public class ResetPasswordRequest {
    private String token;
    private String newPassword;

    //Getter y setter de token
    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token=token;
    }

    //Getter y setter de newPassword
    public String getNewPassword(){
        return newPassword;
    }

    public void setNewPassword(String newPassword){
        this.newPassword=newPassword;
    }
}
