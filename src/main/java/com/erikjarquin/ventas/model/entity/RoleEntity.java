package com.erikjarquin.ventas.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="roles")
public class RoleEntity {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String name;

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

}
