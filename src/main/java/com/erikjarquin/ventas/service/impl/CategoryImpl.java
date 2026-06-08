package com.erikjarquin.ventas.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.erikjarquin.ventas.mapper.CategoryMapper;
import com.erikjarquin.ventas.model.dto.CategoryDto;
import com.erikjarquin.ventas.model.entity.CategoryEntity;
import com.erikjarquin.ventas.repository.CategoryRepository;
import com.erikjarquin.ventas.service.CategoryService;

@Service
public class CategoryImpl implements CategoryService {
    private final CategoryRepository repository;

    public CategoryImpl(CategoryRepository repository){
        this.repository=repository;
    }

    @Override
    public List<CategoryDto> getAll(){
        return repository.findAll()
                .stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto save(CategoryDto dto){
        CategoryEntity entity = CategoryMapper.toEntity(dto);
        CategoryEntity saved = repository.save(entity);

        return CategoryMapper.toDto(saved);
    }
}
