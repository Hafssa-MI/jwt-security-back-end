package com.example.demo.repository;

import com.example.demo.entity.Incident;
import com.example.demo.entity.IncidentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentHistoryRepository extends JpaRepository<IncidentHistory, Long> {
    List<IncidentHistory> findByIncidentOrderByCreatedAtAsc(Incident incident);
}
