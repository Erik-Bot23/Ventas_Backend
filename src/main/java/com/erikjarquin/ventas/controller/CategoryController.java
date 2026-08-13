package com.erikjarquin.ventas.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erikjarquin.ventas.model.dto.ProductsCategories.CategoryDto;
import com.erikjarquin.ventas.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoryController {
    private final CategoryService service;

    public CategoryController(CategoryService service){
        this.service=service;
    }

    @PreAuthorize("hasAuthority('VER_CATEGORIAS')")
    @GetMapping
    public List<CategoryDto> getAll(){
        return service.getAll();
    }

    @PreAuthorize("hasAuthority('CREAR_CATEGORIA')")
    @PostMapping
    public CategoryDto save(@RequestBody CategoryDto dto){
        return service.save(dto);
    }

    @PreAuthorize("hasAuthority('ELIMINAR_CATEGORIA')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.delete(id);
    }
}
