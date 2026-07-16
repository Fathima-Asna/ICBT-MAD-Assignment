package com.printxpress.backend.controller;

import com.printxpress.backend.dto.ApiResponse;
import com.printxpress.backend.model.Product;
import com.printxpress.backend.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAll(@RequestParam(required = false) String category) {
        List<Product> products = category == null || category.isBlank()
                ? productService.findAll()
                : productService.findByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getById(@PathVariable String id) {
        return productService.findById(id)
                .map(p -> ResponseEntity.ok(ApiResponse.success(p)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Product not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> create(@RequestBody Product product) {
        try {
            Product saved = productService.save(product);
            return ResponseEntity.ok(ApiResponse.success("Product created", saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
