package com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO;

import com.example.usermanagementsystem.usermanagementsystem.Enums.Roles;

public record UserPatchDto(String name, Integer age, Roles role, Boolean isActive) {
}
