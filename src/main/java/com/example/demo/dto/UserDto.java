package com.example.demo.dto;

import com.example.demo.entity.Role;

public record UserDto(Long id, String fullName, String email, Role role, boolean enabled) {}

