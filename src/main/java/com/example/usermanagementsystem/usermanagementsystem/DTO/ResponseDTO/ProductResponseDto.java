package com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProductResponseDto(
        Integer id,
        String productName,
        Integer price,
        Integer availableStock,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
