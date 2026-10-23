package com.example.demo.dto;

import com.example.demo.enums.Category;
import com.example.demo.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IncidentRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull Category category,
        @NotNull Priority priority) {}
