package com.example.demo.repository;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    long countByRole(Role role);
    List<AppUser> findByRole(Role role);
}