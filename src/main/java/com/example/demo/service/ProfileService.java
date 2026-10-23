package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.PasswordChangeRequest;
import com.example.demo.dto.ProfileResponse;
import com.example.demo.dto.ProfileUpdateRequest;
import com.example.demo.entity.AppUser;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final AppUserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public ProfileService(AppUserRepository repo, PasswordEncoder encoder,
                          JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    private AppUser current() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return repo.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }

    public ProfileResponse get() {
        AppUser u = current();
        return new ProfileResponse(u.getId(), u.getFullName(), u.getEmail(), u.getRole().name());
    }

    @Transactional
    public AuthResponse update(ProfileUpdateRequest r) {
        AppUser u = current();
        if (!u.getEmail().equalsIgnoreCase(r.email()) && repo.findByEmail(r.email()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email déjà utilisé");
        }
        u.setFullName(r.fullName());
        u.setEmail(r.email());
        repo.save(u);
        // the email is the JWT subject, so the old token stops working: issue a new one
        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(u.getEmail()));
        return new AuthResponse(token, u.getRole().name(), u.getFullName());
    }

    @Transactional
    public void changePassword(PasswordChangeRequest r) {
        AppUser u = current();
        if (!encoder.matches(r.oldPassword(), u.getPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ancien mot de passe incorrect");
        }
        if (!r.newPassword().equals(r.confirmPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La confirmation ne correspond pas");
        }
        u.setPassword(encoder.encode(r.newPassword()));
        repo.save(u);
    }
}
