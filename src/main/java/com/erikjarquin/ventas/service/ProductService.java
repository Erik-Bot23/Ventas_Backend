package com.erikjarquin.ventas.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import com.erikjarquin.ventas.model.dto.ProductDto;
import com.erikjarquin.ventas.model.entity.ProductEntity;

public interface ProductService {
    List<ProductDto> getAll();
    List<ProductDto> getByCategory(String category); //¿Long categoryId?
    
    ProductDto save(
        String name,
        BigDecimal price,
        int stock,
        Long categoryId,
        MultipartFile image
    );

    ProductDto update(
        Long id,
        String name,
        BigDecimal price,
        int stock,
        Long categoryId,
        MultipartFile image
    );

    void delete(Long id);

    ProductDto findByBarcode(String barcode);

    List<ProductDto> search(String q);
} 
