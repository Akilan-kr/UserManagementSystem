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
                .quantity(orderRequestDto.quantity())
                .build();
    }

    public static OrderResponseDto toResponse(Order order) {
        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .productId(order.getProduct().getId())
                .productName(order.getProduct().getProductName())
                .totalAmount(order.getTotalAmount())
                .quantity(order.getQuantity())
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
