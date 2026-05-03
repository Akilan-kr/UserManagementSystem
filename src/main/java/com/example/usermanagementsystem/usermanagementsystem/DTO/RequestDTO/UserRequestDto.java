package com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO;

import com.example.usermanagementsystem.usermanagementsystem.Enums.Roles;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record UserRequestDto(
        @NotBlank(message = "UserName cannot be null or Empty")
        @Pattern(
                regexp = "^[a-zA-Z ]+$",
                message = "Name should only contains Upper Case, Lower Case, Space"
        )
        String name,
        @NotBlank(message = "Email cannot be null or Empty")
        String email,
        @NotNull(message = "Age cannot be null")
        @Min(value = 18)
        @Max(value = 80)
        Integer age,
        @NotBlank(message = "Password cannot be null or Empty")
        @Pattern(
                regexp = "^.*(?=.{8,20})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$$",
                message = "Password should contains minimum 8 maximum 20 character, then it should contain at least 1 Capital letter,1 Small letter, 1 number and 1 special symbol"
        )
        String password,
        @NotNull(message = "Role cannot be null")
        Roles role) {
}
