package com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO;

import com.example.usermanagementsystem.usermanagementsystem.Enums.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record OrderPatchDto(
        @NotNull(message = "quantity cannot be null")
        @Min(value = 1)
        Integer quantity,
        OrderStatus orderStatus
) {
}
