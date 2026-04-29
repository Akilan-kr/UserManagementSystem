package com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO;

public record ApiResponse<T>(
        T data,
        String message,
        Boolean isSuccess
) {
}
