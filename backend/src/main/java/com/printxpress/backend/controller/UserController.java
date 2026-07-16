package com.printxpress.backend.controller;

import com.printxpress.backend.dto.ApiResponse;
import com.printxpress.backend.dto.UsernameLookupRequest;
import com.printxpress.backend.model.User;
import com.printxpress.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getById(@PathVariable String id) {
        return userService.findById(id)
                .map(u -> ResponseEntity.ok(ApiResponse.success(u)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("User not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email is required"));
        }
        Optional<User> existingById = user.getId() != null ? userService.findById(user.getId()) : Optional.empty();
        if (existingById.isPresent()) {
            User saved = userService.save(user);
            return ResponseEntity.ok(ApiResponse.success("User profile updated", saved));
        }
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email already registered"));
        }
        if (user.getUsername() != null && userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Username already taken"));
        }
        User saved = userService.save(user);
        return ResponseEntity.ok(ApiResponse.success("User profile saved", saved));
    }

    @PostMapping("/lookup-email")
    public ResponseEntity<ApiResponse<String>> lookupEmail(@Valid @RequestBody UsernameLookupRequest request) {
        Optional<User> user = userService.findByUsername(request.getUsername());
        if (user.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(user.get().getEmail()));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("Username not found"));
    }
}
