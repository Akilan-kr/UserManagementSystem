package com.example.usermanagementsystem.usermanagementsystem.Mapper;

import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.UserRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.UserResponseDto;
import com.example.usermanagementsystem.usermanagementsystem.Entity.UserInfo;
import com.example.usermanagementsystem.usermanagementsystem.Exception.NotValidAgeException;
import com.example.usermanagementsystem.usermanagementsystem.Exception.NotValidEmailException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UserMapper {

    public static UserInfo toEntity(UserRequestDto userRequestDto) {
                return UserInfo.builder()
                        .name(userRequestDto.name())
                        .email(userRequestDto.email())
                        .age(userRequestDto.age())
                        .password(userRequestDto.password())
                        .role(userRequestDto.role())
                        .build();
    }

    public static UserResponseDto toResponse(UserInfo userInfo) {
        return UserResponseDto.builder()
                .id(userInfo.getId())
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .age(userInfo.getAge())
                .role(userInfo.getRole())
                .updatedAt(userInfo.getUpdateAt())
                .createdAt(userInfo.getCreatedAt())
                .build();
    }
}
