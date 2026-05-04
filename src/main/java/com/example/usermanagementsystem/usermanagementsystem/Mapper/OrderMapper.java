package com.example.usermanagementsystem.usermanagementsystem.Mapper;

import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.OrderRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.OrderResponseDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.PageResponse;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.UserResponseDto;
import com.example.usermanagementsystem.usermanagementsystem.Entity.Order;
import org.springframework.data.domain.Page;

public class OrderMapper {
    public static Order toEntity(OrderRequestDto orderRequestDto) {
        return Order.builder()
                .productName(orderRequestDto.productName())
                .amount(orderRequestDto.amount())
                .build();
    }

    public static OrderResponseDto toResponse(Order order) {
        return OrderResponseDto.builder()
                .id(order.getId())
                .productName(order.getProductName())
                .amount(order.getAmount())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .user(UserMapper.toResponse(order.getUser()))
                .build();
    }

    public static PageResponse<OrderResponseDto> toPageResponse(Page<OrderResponseDto> page){
        return new PageResponse<>(
                page.getContent(),
                page.isEmpty(),
                page.isFirst(),
                page.isLast(),
                page.getNumber() + 1,
                page.getNumberOfElements(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

}
