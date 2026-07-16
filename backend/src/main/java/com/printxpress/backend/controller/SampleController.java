package com.printxpress.backend.controller;

import com.printxpress.backend.dto.ApiResponse;
import com.printxpress.backend.model.Sample;
import com.printxpress.backend.service.SampleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/samples")
@CrossOrigin(origins = "*")
public class SampleController {

    private final SampleService sampleService;

    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Sample>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(sampleService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Sample>> getById(@PathVariable String id) {
        return sampleService.findById(id)
                .map(s -> ResponseEntity.ok(ApiResponse.success(s)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Sample not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Sample>> create(@RequestBody Sample sample) {
        Sample saved = sampleService.save(sample);
        return ResponseEntity.ok(ApiResponse.success("Sample created", saved));
    }
}
