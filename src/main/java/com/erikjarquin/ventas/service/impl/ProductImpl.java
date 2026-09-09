package com.erikjarquin.ventas.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.erikjarquin.ventas.mapper.ProductMapper;
import com.erikjarquin.ventas.model.dto.Products.ProductDto;
import com.erikjarquin.ventas.model.entity.CategoryEntity;
import com.erikjarquin.ventas.model.entity.ProductEntity;
import com.erikjarquin.ventas.repository.CategoryRepository;
import com.erikjarquin.ventas.repository.ProductRepository;
import com.erikjarquin.ventas.service.FileStorageService;
import com.erikjarquin.ventas.service.ProductService;

@Service
public class ProductImpl implements ProductService {
    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    
    public ProductImpl(ProductRepository repository, CategoryRepository categoryRepository, FileStorageService fileStorageService){
        this.repository=repository;
        this.categoryRepository=categoryRepository;
        this.fileStorageService = fileStorageService;
    }

    //Listar todos los productos
    @Override
    public List<ProductDto> getAll(){
        return repository.findAll().stream().map(ProductMapper::toDto).collect(Collectors.toList());
    }

    //Listar productos por categoría
    @Override
    public List<ProductDto> getByCategory(String category){
        return repository.findByCategory_Name(category).stream().map(ProductMapper::toDto).collect(Collectors.toList());
    }

    //Guardar nuevo producto
    @Override
    public ProductDto save(
        String name,
        BigDecimal price,
        int stock,
        Long categoryId,
        String sku,
        String barcode,
        MultipartFile image
    ){
        ProductEntity entity = new ProductEntity();

        entity.setName(name);
        entity.setPrice(price);
        entity.setStock(stock);

        CategoryEntity category = categoryRepository.findById(categoryId).orElseThrow(() -> 
                        new RuntimeException("Categoria no encontrada"));
                                        
        entity.setCategory(category);
        entity.setSku(sku);
        entity.setBarcode(barcode);

        entity.setImg(fileStorageService.store(image));

        ProductEntity saved = repository.save(entity);

        return ProductMapper.toDto(saved);
    }

    //Actualizar producto
    @Override
    public ProductDto update(
        Long id,
        String name,
        BigDecimal price,
        int stock,
        Long categoryId,
        String sku,
        String barcode,
        MultipartFile image
    ){
        ProductEntity entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        entity.setName(name);
        entity.setPrice(price);
        entity.setStock(stock);

        CategoryEntity category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        entity.setCategory(category);
        entity.setSku(sku);
        entity.setBarcode(barcode);

        if(image != null && !image.isEmpty()){
            fileStorageService.delete(entity.getImg());
            entity.setImg(fileStorageService.store(image));
        }

        ProductEntity updated = repository.save(entity);

        return ProductMapper.toDto(updated);
    }

    //Borrar producto
    @Override
    public void delete(Long id){
        ProductEntity entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        fileStorageService.delete(entity.getImg());
        repository.deleteById(id);
    }

    //Buscar productos por código de barras
    @Override
    public ProductDto findByBarcode(String barcode){
        ProductEntity product = repository.findByBarcode(barcode).orElseThrow(() ->
            new RuntimeException("Producto no encontrado"));

        return ProductMapper.toDto(product);
    }

    //Buscador de productos
    @Override
    public List<ProductDto> search(String q){
        return repository.search(q).stream().map(ProductMapper::toDto).collect(Collectors.toList());
    }

}
