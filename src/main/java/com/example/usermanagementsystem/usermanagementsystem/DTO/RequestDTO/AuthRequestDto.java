package com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthRequestDto(
        @NotBlank(message = "Email Cannot be null or empty")
        @Pattern(
                regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
                message = "Email format is invalid (e.g., example@domain.com)"
        )
        String email,
        @NotBlank(message = "Password Cannot be null or empty")
        @Pattern(
                regexp = "^.*(?=.{8,20})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$$",
                message = "Password should contains minimum 8 maximum 20 character, then it should contain at least 1 Capital letter,1 Small letter, 1 number and 1 special symbol"
        )
        String password) {
}
