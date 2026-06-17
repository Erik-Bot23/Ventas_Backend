package com.erikjarquin.ventas.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.erikjarquin.ventas.model.dto.ProductDto;
import com.erikjarquin.ventas.service.ProductService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service){
        this.service = service;
    }

    @GetMapping
    public List<ProductDto> getProducts(@RequestParam(required = false) String category){ //¿Long categoryId?
        if (category != null) {
            return service.getByCategory(category);
        }
        return service.getAll();
    }
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductDto saveProduct(
        @RequestParam("name") String name,
        @RequestParam("price") BigDecimal price,
        @RequestParam("stock") int stock,
        @RequestParam("categoryId") Long categoryId,
        @RequestParam(value = "image", required = false) MultipartFile image
    ){
        return service.save(name, price, stock, categoryId, image); //Me marca erro aqui
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductDto updateProduct(
        @PathVariable Long id,
        @RequestParam String name,
        @RequestParam BigDecimal price,
        @RequestParam int stock,
        @RequestParam Long categoryId,
        @RequestParam(value = "image", required = false) MultipartFile image
    ){
        return service.update(id, name, price, stock, categoryId, image); //Aqui me marca error
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/barcode/{barcode}")
    public ProductDto findByBarcode(@PathVariable String barcode){
        return service.findByBarcode(barcode);
    }

    @GetMapping("/search")
    public List<ProductDto> search(@RequestParam String q){
        return service.search(q);
    }
}
