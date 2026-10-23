package com.example.demo.entity;

import com.example.demo.enums.Category;
import com.example.demo.enums.IncidentStatus;
import com.example.demo.enums.Priority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(length = 2000)
    private String description;
    @Enumerated(EnumType.STRING) private Category category;
    @Enumerated(EnumType.STRING) private Priority priority;
    @Enumerated(EnumType.STRING) private IncidentStatus status = IncidentStatus.OPEN;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser assignedTo;
}

