package com.example.usermanagementsystem.usermanagementsystem.Service.Implementation;

import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.OrderRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.OrderResponseDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.PageResponse;
import com.example.usermanagementsystem.usermanagementsystem.Entity.Order;
import com.example.usermanagementsystem.usermanagementsystem.Entity.UserInfo;
import com.example.usermanagementsystem.usermanagementsystem.Enums.OrderStatus;
import com.example.usermanagementsystem.usermanagementsystem.Mapper.OrderMapper;
import com.example.usermanagementsystem.usermanagementsystem.Repository.OrderRepository;
import com.example.usermanagementsystem.usermanagementsystem.Repository.UserRepository;
import com.example.usermanagementsystem.usermanagementsystem.Service.Interface.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        String loginUserEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        if(loginUserEmail != null) {
            log.info("creating the order for the user");
            Order order = OrderMapper.toEntity(orderRequestDto);
            order.setOrderStatus(OrderStatus.PENDING);
            Optional<UserInfo> user = userRepository.findByEmailAndIsActive(loginUserEmail, true);
            if (user.isPresent()) {
                order.setUser(user.get());
                return OrderMapper.toResponse(orderRepository.save(order));
            } else
                throw new UsernameNotFoundException("User not found with the email"+ loginUserEmail);
        } else
            throw new UsernameNotFoundException("You dont have access, please Login and try again");

    }

    @Override
    public List<OrderResponseDto> getOrdersByUser() {
        String loginUserEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        if (loginUserEmail != null) {
            log.info("Getting Order for the user{}", loginUserEmail);
            Optional<UserInfo> user = userRepository.findByEmailAndIsActive(loginUserEmail, true);
            if (user.isPresent()) {
                return orderRepository.findAllByUserId(user.get().getId()).stream().map(OrderMapper::toResponse).toList();
            }
            else
                throw new UsernameNotFoundException("User not found with the email"+ loginUserEmail);
        } else
            throw new UsernameNotFoundException("You dont have access, please Login and try again");
    }

    @Override
    public List<OrderResponseDto> getOrdersByUser(Integer id) {
        Optional<UserInfo> user = userRepository.findByIdAndIsActive(id, true);
        if(user.isPresent()) {
            return orderRepository.findAllByUserId(user.get().getId()).stream().map(OrderMapper::toResponse).toList();
        }
        else
            throw new UsernameNotFoundException("User not found with the id: "+ id );
    }


    @Cacheable(value = "allorders", key = "#page + '_' + #size + '_' + #ascending + '_' + #sortBy")
    @Override
    public PageResponse<OrderResponseDto> getAllOrders(Integer page, Integer size, Boolean ascending, String sortBy) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of((page <= 0) ? 0 : page - 1, size <= 0 ? 5 : size , sort);
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return OrderMapper.toPageResponse(orderPage.map(OrderMapper::toResponse));
    }
}
