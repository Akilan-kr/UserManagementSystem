package com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

public record OrderRequestDto(
        @NotBlank(message = "Product name cannot be null or Empty")
        @Pattern(
                regexp = "^[a-zA-Z ]+$",
                message = "Name should only contains Upper Case, Lower Case, Space"
        )
        String productName,
        @NotNull(message = "Amount cannot be null")
        @Min(value = 1)
        Integer amount
) {
}
