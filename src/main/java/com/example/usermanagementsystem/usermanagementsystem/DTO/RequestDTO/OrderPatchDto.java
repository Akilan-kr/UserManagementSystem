package com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO;

import com.example.usermanagementsystem.usermanagementsystem.Enums.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record OrderPatchDto(
        @Pattern(
                regexp = "^[a-zA-Z ]+$",
                message = "Name should only contains Upper Case, Lower Case, Space"
        )
        String productName,
        @NotNull(message = "Amount cannot be null")
        @Min(value = 0)
        Integer amount,
        Boolean isActive,
        OrderStatus orderStatus
) {
}
