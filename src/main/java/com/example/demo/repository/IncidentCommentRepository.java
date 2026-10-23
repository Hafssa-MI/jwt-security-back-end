package com.example.demo.repository;

import com.example.demo.entity.Incident;
import com.example.demo.entity.IncidentComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentCommentRepository extends JpaRepository<IncidentComment, Long> {
    List<IncidentComment> findByIncidentOrderByCreatedAtAsc(Incident incident);
}
