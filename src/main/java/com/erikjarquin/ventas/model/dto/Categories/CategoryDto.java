package com.erikjarquin.ventas.model.dto.Categories;

public class CategoryDto {
    private Long id;
    private String name;

    public CategoryDto(){}

    public CategoryDto(Long id, String name){
        this.id=id;
        this.name=name;
    }

    //Getters y setters de id
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    //Getters y setter de name
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }
}
