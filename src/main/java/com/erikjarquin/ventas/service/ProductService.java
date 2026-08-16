package com.erikjarquin.ventas.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.erikjarquin.ventas.model.dto.Products.ProductDto;

public interface ProductService {
    List<ProductDto> getAll();
    List<ProductDto> getByCategory(String category); //¿Long categoryId?
    
    ProductDto save(
        String name,
        BigDecimal price,
        int stock,
        Long categoryId,
        String sku,
        String barcode,
        MultipartFile image
    );

    ProductDto update(
        Long id,
        String name,
        BigDecimal price,
        int stock,
        Long categoryId,
        String sku,
        String barcode,
        MultipartFile image
    );

    void delete(Long id);

    ProductDto findByBarcode(String barcode);

    List<ProductDto> search(String q);
} 
