package com.example.usermanagementsystem.usermanagementsystem.Service.Interface;

import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.OrderPatchDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.OrderRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.UserRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.OrderResponseDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.PageResponse;
import com.example.usermanagementsystem.usermanagementsystem.Entity.Order;

import java.util.List;

public interface IOrderService {
    OrderResponseDto createOrder(OrderRequestDto orderRequestDto);

    List<OrderResponseDto> getOrdersByUser();

    List<OrderResponseDto> getOrdersByUser(Integer id);

    PageResponse<OrderResponseDto> getAllOrders(Integer page, Integer size, Boolean ascending, String sortBy, String name);

    void deleteOrderByOrderId(String orderId);

    void cancelOrderByOrderId(String orderId);

    OrderResponseDto patchOrderById(OrderPatchDto orderPatchDto, String orderId);
}
