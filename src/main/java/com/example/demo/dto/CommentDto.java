package com.example.demo.dto;

import java.time.LocalDateTime;

public record CommentDto(String author, String content, LocalDateTime createdAt) {}

