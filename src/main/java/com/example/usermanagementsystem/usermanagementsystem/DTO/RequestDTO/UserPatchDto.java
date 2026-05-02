package com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO;

import com.example.usermanagementsystem.usermanagementsystem.Enums.Roles;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserPatchDto(
        @Pattern(
                regexp = "^[a-zA-Z ]+$",
                message = "Name should only contains Upper Case, Lower Case, Space"
        )
        String name,
        @Min(value = 18)
        @Max(value = 80)
        Integer age,
        Roles role,
        @Pattern(
                regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
                message = "Email format is invalid (e.g., example@domain.com)"
        )
        String email) {
}
