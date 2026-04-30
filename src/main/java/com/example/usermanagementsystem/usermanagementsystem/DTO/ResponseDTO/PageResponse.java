package com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO;

import lombok.Builder;

import java.util.List;


public record PageResponse<T>(
        List<T> content,
        Boolean empty,
        Boolean first,
        Boolean last,
        Integer number,
        Integer numberOfElements,
        Integer size,
        Integer totalPage,
        Long totalElements
) {
}
