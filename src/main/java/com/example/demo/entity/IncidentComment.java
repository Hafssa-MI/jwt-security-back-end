package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class IncidentComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Incident incident;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser author;
    @Column(length = 2000)
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();
}
