package com.printxpress.backend.service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.printxpress.backend.dto.CreateOrderRequest;
import com.printxpress.backend.dto.OrderItemRequest;
import com.printxpress.backend.model.OrderItem;
import com.printxpress.backend.model.PrintOrder;
import com.printxpress.backend.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final CollectionReference orders;
    private final ProductService productService;

    public OrderService(Firestore firestore, ProductService productService) {
        this.orders = firestore.collection("print_orders");
        this.productService = productService;
    }

    public PrintOrder createOrder(CreateOrderRequest request) {
        PrintOrder order = new PrintOrder();
        order.setUserId(request.getUserId());
        order.setDeliveryId(request.getDeliveryId());
        order.setName(request.getName());
        order.setType(request.getType());
        order.setStatus("PENDING");

        List<OrderItem> items = new ArrayList<>();
        double total = 0;
        for (OrderItemRequest req : request.getItems()) {
            if (req.getQuantity() == null || req.getQuantity() < 1) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            Product product = productService.findById(req.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + req.getProductId()));

            double price = product.getBasePrice() * req.getQuantity();
            OrderItem item = OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(req.getQuantity())
                    .price(price)
                    .designUrl(req.getDesignUrl())
                    .customText(req.getCustomText())
                    .build();
            items.add(item);
            total += price;
        }

        order.setTotalAmount(total);
        order.setOrderItems(items);

        DocumentReference doc = orders.document();
        order.setId(doc.getId());
        try {
            doc.set(order).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to create order", e);
        }
        return order;
    }

    public List<PrintOrder> findByUserId(String userId) {
        try {
            return orders.whereEqualTo("userId", userId).get().get().getDocuments().stream()
                    .map(d -> {
                        PrintOrder o = d.toObject(PrintOrder.class);
                        o.setId(d.getId());
                        return o;
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to load user orders", e);
        }
    }

    public Optional<PrintOrder> findById(String id) {
        try {
            DocumentSnapshot doc = orders.document(id).get().get();
            if (doc.exists()) {
                PrintOrder o = doc.toObject(PrintOrder.class);
                o.setId(doc.getId());
                return Optional.of(o);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to find order", e);
        }
        return Optional.empty();
    }

    public List<PrintOrder> findAll() {
        try {
            return orders.get().get().getDocuments().stream()
                    .map(d -> {
                        PrintOrder o = d.toObject(PrintOrder.class);
                        o.setId(d.getId());
                        return o;
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to load orders", e);
        }
    }
}
