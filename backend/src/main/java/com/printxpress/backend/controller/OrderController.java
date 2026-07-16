package com.printxpress.backend.controller;

import com.printxpress.backend.dto.ApiResponse;
import com.printxpress.backend.dto.CreateOrderRequest;
import com.printxpress.backend.model.PrintOrder;
import com.printxpress.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PrintOrder>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(orderService.findAll()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PrintOrder>>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.findByUserId(userId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PrintOrder>> getById(@PathVariable String id) {
        return orderService.findById(id)
                .map(o -> ResponseEntity.ok(ApiResponse.success(o)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Order not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PrintOrder>> create(@Valid @RequestBody CreateOrderRequest request) {
        try {
            PrintOrder order = orderService.createOrder(request);
            return ResponseEntity.ok(ApiResponse.success("Order created", order));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
