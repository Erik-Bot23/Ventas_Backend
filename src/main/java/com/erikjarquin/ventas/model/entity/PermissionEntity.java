package com.erikjarquin.ventas.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "permissions")
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "permissions")
    @JsonIgnore
    private List<RoleEntity> roles = new ArrayList<>();

    public PermissionEntity(){}

    public PermissionEntity(Long id, String name){
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

    //Getter y setter de nombre
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    //Getter y setter de roles
    public List<RoleEntity> getRoles(){
        return roles;
    }

    public void setRoles(List<RoleEntity> roles){
        this.roles=roles;
    }

    
}
