package com.erikjarquin.ventas.service;

import java.util.List;

import com.erikjarquin.ventas.model.dto.Categories.CategoryDto;

public interface CategoryService {
    List<CategoryDto> getAll();
    CategoryDto save(CategoryDto dto);
    void delete(Long id);
}
