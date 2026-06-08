package com.erikjarquin.ventas.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="products")
public class ProductEntity {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int stock;

    @ManyToOne(fetch=FetchType.LAZY)//No genera posibles consultas dobles, una por cada producto
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    private String img;

    public ProductEntity(){}

    //getter y setter de id
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    //getter y setter de name
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    //getter y setter de price
    public double getPrice(){
        return price;
    }

    public void setPrice(double price){
        this.price=price;
    }

    //getter y setter de stock
    public int getStock(){
        return stock;
    }

    public void setStock(int stock){
        this.stock=stock;
    }

    //getter y setter de category
    public CategoryEntity getCategory(){
        return category;
    }

    public void setCategory(CategoryEntity category){
        this.category=category;
    }

    //getter y setter de img
    public String getImg(){
        return img;
    }

    public void setImg(String img){
        this.img=img;
    }
}
