package com.erikjarquin.ventas.model.dto.ResetPassword;

public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;

    //Getter y setter de currentPassword
    public String getCurrentPassword(){
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword){
        this.currentPassword=currentPassword;
    }

    //Getter y setter de newPassword
    public String getNewPassword(){
        return newPassword;
    }

    public void setNewPassword(String newPassword){
        this.newPassword=newPassword;
    }
}
