package com.printxpress.backend.service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.printxpress.backend.model.Delivery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    private final CollectionReference deliveries;

    public DeliveryService(Firestore firestore) {
        this.deliveries = firestore.collection("deliveries");
    }

    public List<Delivery> findAll() {
        try {
            return deliveries.get().get().getDocuments().stream()
                    .map(d -> {
                        Delivery delivery = d.toObject(Delivery.class);
                        delivery.setId(d.getId());
                        return delivery;
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to load deliveries", e);
        }
    }

    public Optional<Delivery> findById(String id) {
        try {
            DocumentSnapshot doc = deliveries.document(id).get().get();
            if (doc.exists()) {
                Delivery d = doc.toObject(Delivery.class);
                d.setId(doc.getId());
                return Optional.of(d);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to find delivery", e);
        }
        return Optional.empty();
    }

    public Delivery save(Delivery delivery) {
        if (delivery.getId() == null || delivery.getId().isBlank()) {
            DocumentReference doc = deliveries.document();
            delivery.setId(doc.getId());
        }
        try {
            deliveries.document(delivery.getId()).set(delivery).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save delivery", e);
        }
        return delivery;
    }
}
