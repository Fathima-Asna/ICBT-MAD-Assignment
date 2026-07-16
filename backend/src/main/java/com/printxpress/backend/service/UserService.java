package com.printxpress.backend.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.printxpress.backend.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private final Firestore firestore;
    private final CollectionReference users;

    public UserService(Firestore firestore) {
        this.firestore = firestore;
        this.users = firestore.collection("users");
    }

    public User save(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            DocumentReference doc = users.document();
            user.setId(doc.getId());
        }
        try {
            users.document(user.getId()).set(user).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save user", e);
        }
        return user;
    }

    public Optional<User> findById(String id) {
        try {
            DocumentSnapshot doc = users.document(id).get().get();
            if (doc.exists()) {
                return Optional.of(doc.toObject(User.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to find user", e);
        }
        return Optional.empty();
    }

    public Optional<User> findByEmail(String email) {
        try {
            ApiFuture<QuerySnapshot> future = users.whereEqualTo("email", email).get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            if (!docs.isEmpty()) {
                User user = docs.get(0).toObject(User.class);
                user.setId(docs.get(0).getId());
                return Optional.of(user);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to find user by email", e);
        }
        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) {
        try {
            ApiFuture<QuerySnapshot> future = users.whereEqualTo("username", username).get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            if (!docs.isEmpty()) {
                User user = docs.get(0).toObject(User.class);
                user.setId(docs.get(0).getId());
                return Optional.of(user);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to find user by username", e);
        }
        return Optional.empty();
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }
}
