package com.erikjarquin.ventas.model.entity;

import com.erikjarquin.ventas.model.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {
    
    @Id
    //@Column(length = 20)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    //Anteriormente era String ahora será Long
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(unique=true, nullable=false)
    private String email;

    @Column(nullable=false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;

    //getter y setter de id
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    //getter y setter de nombre
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    //getter y setter de email
    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email=email;
    }

    //getter y setter para password 
    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password=password;
    }

    //getter y setter de role
    public Role getRole(){
        return role;
    }

    public void setRole(Role role){
        this.role=role;
    }

    //getter y setter para boolean
    public boolean isActive(){
        return active;
    }

    public void setActive(boolean active){
        this.active=active;
    }
}