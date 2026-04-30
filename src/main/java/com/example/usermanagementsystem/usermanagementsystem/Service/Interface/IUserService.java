package com.example.usermanagementsystem.usermanagementsystem.Service.Interface;

import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.UserPatchDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.UserRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {

    UserResponseDto createUser(UserRequestDto userRequestDto);

    Page<UserResponseDto> getAllUsers(Pageable pageable, String search);

    UserResponseDto getUserById(Integer id);

    UserResponseDto updateUser(UserRequestDto userRequestDto);

    void deleteUserById(Integer id);

    List<UserResponseDto> CreateBulkUser(List<UserRequestDto> listOfUserRequestDto);

    UserResponseDto patchUser(UserPatchDto userPatchDto, Integer id);
}
