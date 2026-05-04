package com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderRequestDto(
        @NotBlank(message = "Product name cannot be null or Empty")
        String productName,
        @NotNull(message = "Amount cannot be null")
        @Min(value = 0)
        Integer amount
) {
}
