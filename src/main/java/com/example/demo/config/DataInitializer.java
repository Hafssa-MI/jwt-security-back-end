package com.example.demo.config;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.Role;
import com.example.demo.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository repo;
    private final PasswordEncoder encoder;

    public DataInitializer(AppUserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        create("Admin", "admin@test.com", "admin123", Role.ADMIN);
        create("Technicien A", "tech@test.com", "tech123", Role.TECHNICIAN);
    }

    private void create(String name, String email, String pwd, Role role) {
        if (repo.findByEmail(email).isPresent()) return;
        AppUser u = new AppUser();
        u.setFullName(name);
        u.setEmail(email);
        u.setPassword(encoder.encode(pwd));
        u.setRole(role);
        u.setEnabled(true);
        repo.save(u);
    }
}
