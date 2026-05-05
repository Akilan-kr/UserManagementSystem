package com.example.usermanagementsystem.usermanagementsystem.Service.Implementation;

import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.OrderPatchDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.OrderRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.OrderResponseDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.PageResponse;
import com.example.usermanagementsystem.usermanagementsystem.Entity.Order;
import com.example.usermanagementsystem.usermanagementsystem.Entity.UserInfo;
import com.example.usermanagementsystem.usermanagementsystem.Enums.OrderStatus;
import com.example.usermanagementsystem.usermanagementsystem.Exception.OrderNotFoundException;
import com.example.usermanagementsystem.usermanagementsystem.Mapper.OrderMapper;
import com.example.usermanagementsystem.usermanagementsystem.Repository.OrderRepository;
import com.example.usermanagementsystem.usermanagementsystem.Repository.UserRepository;
import com.example.usermanagementsystem.usermanagementsystem.Service.Interface.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.connection.SortParameters;
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
            Order order = OrderMapper.toEntity(orderRequestDto);
            order.setOrderStatus(OrderStatus.PENDING);
            order.setIsActive(true);
            Optional<UserInfo> user = userRepository.findByEmailAndIsActive(loginUserEmail, true);
            if (user.isPresent()) {
                order.setUser(user.get());
                log.info("creating the order for the user");
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
            Optional<UserInfo> user = userRepository.findByEmailAndIsActive(loginUserEmail, true);
            if (user.isPresent()) {
                log.info("Getting Order for the user({})", loginUserEmail);
                return orderRepository.findAllByUserIdAndIsActiveTrue(user.get().getId()).stream().map(OrderMapper::toResponse).toList();
            }
            else
                throw new UsernameNotFoundException("User not found with the email"+ loginUserEmail);
        } else
            throw new UsernameNotFoundException("You dont have access, please Login and try again");
    }

    @Cacheable(value = "orders" , key = "#id")
    @Override
    public List<OrderResponseDto> getOrdersByUser(Integer id) {
        Optional<UserInfo> user = userRepository.findByIdAndIsActive(id, true);
        if(user.isPresent()) {
            log.info("Getting Order for the user with id: {}", id);
            return orderRepository.findAllByUserIdAndIsActiveTrue(user.get().getId()).stream().map(OrderMapper::toResponse).toList();
        }
        else
            throw new UsernameNotFoundException("User not found with the id: "+ id );
    }


    @Cacheable(value = "allorders", key = "#page + '_' + #size + '_' + #ascending + '_' + #sortBy + '_' + #search")
    @Override
    public PageResponse<OrderResponseDto> getAllOrders(Integer page, Integer size, Boolean ascending, String sortBy, String search) {
        log.info("Getting All Order");
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of((page <= 0) ? 0 : page - 1, size <= 0 ? 5 : size , sort);
        Page<Order> orderPage = orderRepository.findByProductNameContainingIgnoreCaseAndIsActiveTrue(pageable, search);
        return OrderMapper.toPageResponse(orderPage.map(OrderMapper::toResponse));
    }

    @Override
    public void deleteOrderById(Integer id) {
        Optional<Order> order = orderRepository.findById(id);
        if(order.isPresent()) {
            log.info("Deleted Order with order id: {}",id);
            order.get().setIsActive(false);
            orderRepository.save(order.get());
        }
        else
            throw new OrderNotFoundException("No Order Found with id: "+id);
    }

    @Override
    public void cancelOrderById(Integer id) {
        String loginUserEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
            Optional<UserInfo> user = userRepository.findByEmailAndIsActive(loginUserEmail, true);
            if(user.isPresent()){
                Optional<Order> order = orderRepository.findByIdAndIsActiveTrue(id);
                if(order.isPresent()){
                    Order availableOrder = order.get();
                    if(availableOrder.getUser().getId().equals(user.get().getId())){
                        if(OrderStatus.PENDING.equals(availableOrder.getOrderStatus())) {
                            log.info("Order({}) was cancel by the user({})", id, loginUserEmail);
                            availableOrder.setOrderStatus(OrderStatus.CANCELLED);
                            orderRepository.save(availableOrder);
                        } else throw new OrderNotFoundException("You cannot Cancel the order which is Completed");
                    } else throw new OrderNotFoundException("You cannot Change the order status which is not belong to you");
                } else throw new OrderNotFoundException("Order Not found with id:"+id);
            } else throw new UsernameNotFoundException("User not found with the email"+ loginUserEmail);
    }

    @Override
    public OrderResponseDto patchOrderById(OrderPatchDto orderPatchDto, Integer id) {
        Optional<Order> optionalOrder = orderRepository.findByIdAndIsActiveTrue(id);
        if(optionalOrder.isPresent()){
            Order order = optionalOrder.get();
            if(!(order.getOrderStatus().equals(OrderStatus.COMPLETED))) {
                if (orderPatchDto.productName() != null)
                    order.setProductName(orderPatchDto.productName());
                if (orderPatchDto.amount() != null)
                    order.setAmount(orderPatchDto.amount());
                if (orderPatchDto.isActive() != null)
                    order.setIsActive(orderPatchDto.isActive());
                if (orderPatchDto.orderStatus() != null)
                    order.setOrderStatus(orderPatchDto.orderStatus());
                log.info("Partial update for the orderId: {}", id);
                return OrderMapper.toResponse(orderRepository.save(order));
            } else
                throw new OrderNotFoundException("You cannot update the order which is already Completed");
        } else
            throw new OrderNotFoundException("No order found with id: "+id);
    }
}

