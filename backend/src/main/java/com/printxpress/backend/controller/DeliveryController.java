package com.printxpress.backend.controller;

import com.printxpress.backend.dto.ApiResponse;
import com.printxpress.backend.model.Delivery;
import com.printxpress.backend.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@CrossOrigin(origins = "*")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Delivery>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(deliveryService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Delivery>> getById(@PathVariable String id) {
        return deliveryService.findById(id)
                .map(d -> ResponseEntity.ok(ApiResponse.success(d)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Delivery not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Delivery>> create(@RequestBody Delivery delivery) {
        Delivery saved = deliveryService.save(delivery);
        return ResponseEntity.ok(ApiResponse.success("Delivery option created", saved));
    }
}
