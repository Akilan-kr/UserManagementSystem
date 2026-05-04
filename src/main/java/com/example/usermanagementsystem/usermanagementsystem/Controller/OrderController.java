package com.example.usermanagementsystem.usermanagementsystem.Controller;

import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.OrderRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.UserRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.ApiResponse;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.OrderResponseDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.PageResponse;
import com.example.usermanagementsystem.usermanagementsystem.Entity.Order;
import com.example.usermanagementsystem.usermanagementsystem.Service.Interface.IOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(@RequestBody OrderRequestDto orderRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(orderService.createOrder(orderRequestDto), "Order Created Successfully", true));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/getOrders")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getOrdersByUser(){
        List<OrderResponseDto> listOfOrderResponseDto = orderService.getOrdersByUser();
        if(!(listOfOrderResponseDto.isEmpty())) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(listOfOrderResponseDto, "Successfully got the order", true));
        } else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(listOfOrderResponseDto, "There is no Orders in the list for the current user", true));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/getOrders/{id}")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getOrdersByUser(@PathVariable Integer id){
        List<OrderResponseDto> listOfOrderResponseDto = orderService.getOrdersByUser(id);
        if(!(listOfOrderResponseDto.isEmpty())) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(listOfOrderResponseDto, "Successfully got the order", true));
        } else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(listOfOrderResponseDto, "There is no Orders in the list for the user with id: "+ id, true));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllOrders")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponseDto>>> getAllOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {
        PageResponse<OrderResponseDto> pageOrderResponse = orderService.getAllOrders(page, size, ascending, sortBy);
        if(!(pageOrderResponse.content().isEmpty()))
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(pageOrderResponse, "Successfully got all the Order", true));
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(pageOrderResponse, "No Orders In the list", true));
    }
}
