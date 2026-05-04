package com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO;

import com.example.usermanagementsystem.usermanagementsystem.Enums.Roles;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
public record UserResponseDto(
        Integer id,
        String name,
        String email,
        Integer age,
        Roles role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt)

    implements Serializable
{
}
