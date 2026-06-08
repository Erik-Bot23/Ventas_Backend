package com.erikjarquin.ventas.model.dto;


public class UpdateUserRequest {
    private String name;
    private String email;
    private String role;
    private boolean active;

    //getters y setters
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
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

    public boolean isActive(){
        return active;
    }

    public void setActive(boolean active){
        this.active=active;
    }
}
