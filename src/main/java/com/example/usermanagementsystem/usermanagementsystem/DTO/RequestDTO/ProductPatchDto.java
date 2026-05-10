package com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ProductPatchDto(
        @Pattern(
               regexp = "^[a-zA-Z ]+$",
               message = "Product Name should only contains Upper Case, Lower Case, Space"
        )
        String productName,
        @NotNull(message = "Price Cannot be null")
        @Min(value = 0)
        Integer price,
        @NotNull(message = "Available Stock cannot be null")
        @Min(value = 1)
        Integer availableStock) {
}
