package com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

public record OrderRequestDto(
        @NotBlank(message = "Product Id cannot be null or Empty")
        Integer productId,
        @NotNull(message = "Count cannot be null")
        @Min(value = 2)
        Integer quantity
) {
}
