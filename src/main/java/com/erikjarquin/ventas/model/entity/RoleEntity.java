package com.erikjarquin.ventas.model.entity;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="roles")
public class RoleEntity {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "role")
    private List<UserEntity> users;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"),
                inverseJoinColumns = @JoinColumn(name="permission_id"))
    private List<PermissionEntity> permissions = new ArrayList<>();

    public RoleEntity(){}

    public RoleEntity(Long id, String name){
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
        this.name = name;
    }

    //Getter y setter de users
    public List<UserEntity> getUsers(){
        return users;
    }

    public void setUsers(List<UserEntity> users){
        this.users = users;
    }

    //Getter y setter de permissions
    public List<PermissionEntity> getPermissions(){
        return permissions;
    } 

    public void setPermissions(List<PermissionEntity> permissions){
        this.permissions=permissions;
    }

}
