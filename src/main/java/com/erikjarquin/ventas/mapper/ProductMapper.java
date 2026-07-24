package com.erikjarquin.ventas.mapper;

import com.erikjarquin.ventas.model.dto.ProductsCategories.ProductDto;
import com.erikjarquin.ventas.model.entity.ProductEntity;

public class ProductMapper {
    public static ProductDto toDto(ProductEntity entity){
        if(entity==null) return null;

        ProductDto dto = new ProductDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setStock(entity.getStock());
        dto.setImg(entity.getImg());

        if(entity.getCategory() != null){
            dto.setCategoryId(entity.getCategory().getId());
            dto.setCategoryName(entity.getCategory().getName());
        }

        dto.setSku(entity.getSku());
        dto.setBarcode(entity.getBarcode());

        return dto;
    }
}
