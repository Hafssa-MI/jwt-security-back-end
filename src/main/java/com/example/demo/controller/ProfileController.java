package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.PasswordChangeRequest;
import com.example.demo.dto.ProfileResponse;
import com.example.demo.dto.ProfileUpdateRequest;
import com.example.demo.service.ProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) { this.service = service; }

    @GetMapping
    public ProfileResponse get() { return service.get(); }

    @PutMapping
    public AuthResponse update(@Valid @RequestBody ProfileUpdateRequest r) { return service.update(r); }

    @PutMapping("/password")
    public Map<String, String> password(@Valid @RequestBody PasswordChangeRequest r) {
        service.changePassword(r);
        return Map.of("message", "Mot de passe modifié");
    }
}