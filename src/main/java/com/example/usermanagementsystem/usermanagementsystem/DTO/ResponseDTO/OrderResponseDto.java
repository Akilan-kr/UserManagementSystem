package com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO;

import com.example.usermanagementsystem.usermanagementsystem.Entity.UserInfo;
import com.example.usermanagementsystem.usermanagementsystem.Enums.OrderStatus;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
public record OrderResponseDto(
        Integer id,
        String productName,
        Integer amount,
        OrderStatus orderStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UserResponseDto user
) implements Serializable {
}

