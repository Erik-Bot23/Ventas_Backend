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

import com.erikjarquin.ventas.model.dto.CategoryDto;
import com.erikjarquin.ventas.service.CategoryService;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoryController {
    private final CategoryService service;

    public CategoryController(CategoryService service){
        this.service=service;
    }

    @GetMapping
    public List<CategoryDto> getAll(){
        return service.getAll();
    }

    @PostMapping
    public CategoryDto save(@RequestBody CategoryDto dto){
        return service.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.delete(id);
    }
}
