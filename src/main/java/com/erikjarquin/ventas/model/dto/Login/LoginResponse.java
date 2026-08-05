package com.erikjarquin.ventas.model.dto.Login;

import java.util.List;

public class LoginResponse {
    private boolean success;
    private Long id;
    private String name;
    private String email;
    private String role;
    private List<String> permissions;
    private String token;

    public LoginResponse(boolean success, Long id, String name, String email, String role, List<String> permissions, String token){
        this.success=success;
        this.id=id;
        this.name=name;
        this.email=email;
        this.role = role;
        this.permissions=permissions;
        this.token=token;
    }

    public boolean isSuccess(){
        return success;
    }

    public void setSuccess(boolean success){
        this.success=success;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email=email;
    }

    public String getRole(){
        return role;
    }

    public void setRole(String role){
        this.role=role;
    }

    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token=token;
    }

    public List<String> getPermissions(){
        return permissions;
    }

    public void setPermissions(List<String> permissions){
        this.permissions=permissions;
    }
}
