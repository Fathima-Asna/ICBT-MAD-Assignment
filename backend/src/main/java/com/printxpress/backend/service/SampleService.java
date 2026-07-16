package com.printxpress.backend.service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.printxpress.backend.model.Sample;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class SampleService {

    private final CollectionReference samples;

    public SampleService(Firestore firestore) {
        this.samples = firestore.collection("samples");
    }

    public List<Sample> findAll() {
        try {
            return samples.get().get().getDocuments().stream()
                    .map(d -> {
                        Sample s = d.toObject(Sample.class);
                        s.setId(d.getId());
                        return s;
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to load samples", e);
        }
    }

    public Optional<Sample> findById(String id) {
        try {
            DocumentSnapshot doc = samples.document(id).get().get();
            if (doc.exists()) {
                Sample s = doc.toObject(Sample.class);
                s.setId(doc.getId());
                return Optional.of(s);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to find sample", e);
        }
        return Optional.empty();
    }

    public List<Sample> findByUserId(String userId) {
        try {
            return samples.whereEqualTo("userId", userId).get().get().getDocuments().stream()
                    .map(d -> {
                        Sample s = d.toObject(Sample.class);
                        s.setId(d.getId());
                        return s;
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to load user samples", e);
        }
    }

    public Sample save(Sample sample) {
        if (sample.getId() == null || sample.getId().isBlank()) {
            DocumentReference doc = samples.document();
            sample.setId(doc.getId());
        }
        try {
            samples.document(sample.getId()).set(sample).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save sample", e);
        }
        return sample;
    }
}
