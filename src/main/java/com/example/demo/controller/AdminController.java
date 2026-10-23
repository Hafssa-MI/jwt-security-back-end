package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.AppUser;
import com.example.demo.entity.Role;
import com.example.demo.enums.IncidentStatus;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.AppUserRepository;
import com.example.demo.repository.IncidentRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AppUserRepository userRepo;
    private final IncidentRepository incidentRepo;

    public AdminController(AppUserRepository userRepo, IncidentRepository incidentRepo) {
        this.userRepo = userRepo;
        this.incidentRepo = incidentRepo;
    }

    @GetMapping("/users")
    public List<UserDto> users() {
        return userRepo.findAll().stream()
                .map(u -> new UserDto(u.getId(), u.getFullName(), u.getEmail(), u.getRole(), u.isEnabled()))
                .toList();
    }

    @PutMapping("/users/{id}/enable")
    public UserDto enable(@PathVariable Long id) { return setEnabled(id, true); }

    @PutMapping("/users/{id}/disable")
    public UserDto disable(@PathVariable Long id, Authentication auth) {
        AppUser u = userRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        if (u.getEmail().equals(auth.getName())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Vous ne pouvez pas désactiver votre propre compte");
        }
        return setEnabled(id, false);
    }

    private UserDto setEnabled(Long id, boolean enabled) {
        AppUser u = userRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        u.setEnabled(enabled);
        userRepo.save(u);
        return new UserDto(u.getId(), u.getFullName(), u.getEmail(), u.getRole(), u.isEnabled());
    }

    @GetMapping("/statistics")
    public Map<String, Long> statistics() {
        Map<String, Long> m = new LinkedHashMap<>();
        m.put("totalUsers", userRepo.count());
        m.put("employees", userRepo.countByRole(Role.EMPLOYEE));
        m.put("technicians", userRepo.countByRole(Role.TECHNICIAN));
        m.put("totalIncidents", incidentRepo.count());
        m.put("open", incidentRepo.countByStatus(IncidentStatus.OPEN));
        m.put("inProgress", incidentRepo.countByStatus(IncidentStatus.IN_PROGRESS));
        m.put("closed", incidentRepo.countByStatus(IncidentStatus.CLOSED));
        return m;
    }
}
