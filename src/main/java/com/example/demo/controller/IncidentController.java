package com.example.demo.controller;

import com.example.demo.dto.CommentRequest;
import com.example.demo.dto.IncidentRequest;
import com.example.demo.dto.IncidentResponse;
import com.example.demo.dto.StatusRequest;
import com.example.demo.service.IncidentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@SecurityRequirement(name = "bearerAuth")
public class IncidentController {

    private final IncidentService service;

    public IncidentController(IncidentService service) { this.service = service; }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public IncidentResponse create(@Valid @RequestBody IncidentRequest r) { return service.create(r); }

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<IncidentResponse> my() { return service.my(); }

    @GetMapping("/assigned")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public List<IncidentResponse> assigned() { return service.assigned(); }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<IncidentResponse> all() { return service.all(); }

    @GetMapping("/{id}")
    public IncidentResponse get(@PathVariable Long id) { return service.get(id); }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('TECHNICIAN','ADMIN')")
    public IncidentResponse status(@PathVariable Long id, @Valid @RequestBody StatusRequest r) {
        return service.updateStatus(id, r.status());
    }

    @PutMapping("/{id}/assign/{technicianId}")
    @PreAuthorize("hasRole('ADMIN')")
    public IncidentResponse assign(@PathVariable Long id, @PathVariable Long technicianId) {
        return service.assign(id, technicianId);
    }

    @PostMapping("/{id}/comments")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public IncidentResponse comment(@PathVariable Long id, @Valid @RequestBody CommentRequest r) {
        return service.addComment(id, r.content());
    }
}
