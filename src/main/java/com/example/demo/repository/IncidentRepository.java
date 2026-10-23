package com.example.demo.repository;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.Incident;
import com.example.demo.enums.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByCreatedByOrderByCreatedAtDesc(AppUser user);
    List<Incident> findByAssignedToOrderByCreatedAtDesc(AppUser user);
    List<Incident> findAllByOrderByCreatedAtDesc();
    long countByStatus(IncidentStatus status);
}
