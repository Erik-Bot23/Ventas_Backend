package com.erikjarquin.ventas.model.dto.Permissions;

public class PermissionResponse {
    private Long id;
    private String name;

    public PermissionResponse(){}

    public PermissionResponse(Long id, String name){
        this.id=id;
        this.name=name;
    }

    //Getter y setter de id
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    //Getter y setter de name
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    
}
