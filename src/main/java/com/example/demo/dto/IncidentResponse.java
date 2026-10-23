package com.example.demo.dto;

import com.example.demo.enums.Category;
import com.example.demo.enums.IncidentStatus;
import com.example.demo.enums.Priority;

import java.time.LocalDateTime;
import java.util.List;

public record IncidentResponse(
        Long id, String title, String description,
        Category category, Priority priority, IncidentStatus status,
        LocalDateTime createdAt, LocalDateTime updatedAt,
        String createdBy, String assignedTo,
        List<HistoryDto> history, List<CommentDto> comments) {}
