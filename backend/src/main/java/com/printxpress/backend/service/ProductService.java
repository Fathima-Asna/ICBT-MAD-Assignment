package com.printxpress.backend.service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.printxpress.backend.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final CollectionReference products;

    public ProductService(Firestore firestore) {
        this.products = firestore.collection("products");
    }

    public List<Product> findAll() {
        try {
            return products.get().get().getDocuments().stream()
                    .map(d -> {
                        Product p = d.toObject(Product.class);
                        p.setId(d.getId());
                        return p;
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to load products", e);
        }
    }

    public List<Product> findByCategory(String category) {
        try {
            return products.whereEqualTo("category", category).get().get().getDocuments().stream()
                    .map(d -> {
                        Product p = d.toObject(Product.class);
                        p.setId(d.getId());
                        return p;
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to load products by category", e);
        }
    }

    public Optional<Product> findById(String id) {
        try {
            DocumentSnapshot doc = products.document(id).get().get();
            if (doc.exists()) {
                Product p = doc.toObject(Product.class);
                p.setId(doc.getId());
                return Optional.of(p);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to find product", e);
        }
        return Optional.empty();
    }

    public Product save(Product product) {
        if (product.getBasePrice() == null || product.getBasePrice() < 0) {
            throw new IllegalArgumentException("Base price must be positive");
        }
        if (product.getId() == null || product.getId().isBlank()) {
            DocumentReference doc = products.document();
            product.setId(doc.getId());
        }
        try {
            products.document(product.getId()).set(product).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save product", e);
        }
        return product;
    }
}
