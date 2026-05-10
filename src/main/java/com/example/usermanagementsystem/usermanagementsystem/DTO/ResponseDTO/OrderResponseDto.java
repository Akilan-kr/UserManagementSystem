package com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO;

import com.example.usermanagementsystem.usermanagementsystem.Entity.UserInfo;
import com.example.usermanagementsystem.usermanagementsystem.Enums.OrderStatus;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
public record OrderResponseDto(
        String orderId,
        Integer productId,
        String productName,
        Integer totalAmount,
        Integer quantity,
        OrderStatus orderStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UserResponseDto user
) implements Serializable {
}

