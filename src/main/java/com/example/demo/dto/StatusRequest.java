package com.example.demo.dto;

import com.example.demo.enums.IncidentStatus;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull IncidentStatus status) {}

