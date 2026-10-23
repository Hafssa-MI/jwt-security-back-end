package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(
        @NotBlank String oldPassword,
        @NotBlank @Size(min = 6, message = "Mot de passe trop court (6 caractères min)") String newPassword,
        @NotBlank String confirmPassword) {}
