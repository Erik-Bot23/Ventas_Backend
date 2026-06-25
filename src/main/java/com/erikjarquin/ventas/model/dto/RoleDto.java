package com.erikjarquin.ventas.model.dto;

public class RoleDto {
    private Long id;
    private String role;

    public RoleDto(){}

    public RoleDto(Long id, String role){
        this.id=id;
        this.role=role;
    }

    //Getter y setter de id
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    //Getter y setter de name
    public String getRole(){
        return role;
    }

    public void setRole(String role){
        this.role=role;
    }
}
