package com.example.usermanagementsystem.usermanagementsystem.Service.Implementation;

import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.OrderPatchDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.OrderRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.OrderResponseDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.PageResponse;
import com.example.usermanagementsystem.usermanagementsystem.Entity.Order;
import com.example.usermanagementsystem.usermanagementsystem.Entity.Product;
import com.example.usermanagementsystem.usermanagementsystem.Entity.UserInfo;
import com.example.usermanagementsystem.usermanagementsystem.Enums.OrderStatus;
import com.example.usermanagementsystem.usermanagementsystem.Exception.ExceedingStockCountException;
import com.example.usermanagementsystem.usermanagementsystem.Exception.OrderNotFoundException;
import com.example.usermanagementsystem.usermanagementsystem.Exception.ProductNotFoundException;
import com.example.usermanagementsystem.usermanagementsystem.Mapper.OrderMapper;
import com.example.usermanagementsystem.usermanagementsystem.Mapper.PageMapper;
import com.example.usermanagementsystem.usermanagementsystem.Repository.OrderRepository;
import com.example.usermanagementsystem.usermanagementsystem.Repository.ProductRepository;
import com.example.usermanagementsystem.usermanagementsystem.Repository.UserRepository;
import com.example.usermanagementsystem.usermanagementsystem.Service.CommonUtils;
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
    private final ProductRepository productRepository;
    private final CommonUtils commonUtils;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository, CommonUtils commonUtils) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.commonUtils = commonUtils;
    }

    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        String loginUserEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        UserInfo user = userRepository.findByEmailAndIsActive(loginUserEmail, true).orElseThrow(() -> new UsernameNotFoundException("There is no user found with email " + loginUserEmail));
        Product product = productRepository.findByIdAndIsActiveTrue(orderRequestDto.productId()).orElseThrow(()-> new ProductNotFoundException("There is no Product with id: "+orderRequestDto.productId()));
        if(loginUserEmail != null) {
            if(product.getAvailableStock() >= orderRequestDto.quantity()) {
                Order order = OrderMapper.toEntity(orderRequestDto);
                order.setProduct(product);
                order.setOrderStatus(OrderStatus.PENDING);
                order.setIsActive(true);
                order.setUser(user);
                order.setTotalAmount(orderRequestDto.quantity() * product.getPrice());
                order.setOrderId(commonUtils.generateOrderId());
                product.setAvailableStock(product.getAvailableStock() - orderRequestDto.quantity());
                productRepository.save(product);
                log.info("creating the order for the user");
                return OrderMapper.toResponse(orderRepository.save(order));
            } else
                throw new ExceedingStockCountException("Available stock is only "+product.getAvailableStock()+" try to place a valid order within the available stock");
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
        UserInfo user = userRepository.findByIdAndIsActive(id, true).orElseThrow(() -> new UsernameNotFoundException("User not found with the id: "+ id ));
        log.info("Getting Order for the user with id: {}", id);
        return orderRepository.findAllByUserIdAndIsActiveTrue(user.getId()).stream().map(OrderMapper::toResponse).toList();
    }


    @Cacheable(value = "allorders", key = "#page + '_' + #size + '_' + #ascending + '_' + #sortBy + '_' + #search")
    @Override
    public PageResponse<OrderResponseDto> getAllOrders(Integer page, Integer size, Boolean ascending, String sortBy, String search) {
        log.info("Getting All Order");
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of((page <= 0) ? 0 : page - 1, size <= 0 ? 5 : size , sort);
        Page<Order> orderPage = orderRepository.findByAndIsActiveTrue(pageable, search);
        return PageMapper.toPageResponse(orderPage.map(OrderMapper::toResponse));
    }

    @Override
    public void deleteOrderByOrderId(String orderId) {
        Optional<Order> order = orderRepository.findByOrderIdAndIsActiveTrue(orderId);
        if(order.isPresent()) {
            log.info("Deleted Order with order id: {}",orderId);
            order.get().setIsActive(false);
            orderRepository.save(order.get());
        }
        else
            throw new OrderNotFoundException("No Order Found with id: "+orderId);
    }

    @Override
    public void cancelOrderByOrderId(String orderId) {
        String loginUserEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        UserInfo user = userRepository.findByEmailAndIsActive(loginUserEmail, true).orElseThrow(()-> new UsernameNotFoundException("User not found with the email"+ loginUserEmail));
        Order order = orderRepository.findByOrderIdAndIsActiveTrue(orderId).orElseThrow(() -> new OrderNotFoundException("Order Not found with id:"+orderId));
        if(order.getUser().getId().equals(user.getId())){
            if(OrderStatus.PENDING.equals(order.getOrderStatus())) {
                log.info("Order({}) was cancel by the user({})", orderId, loginUserEmail);
                order.setOrderStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
            } else throw new OrderNotFoundException("You cannot Cancel the order which is Completed");
        } else throw new OrderNotFoundException("You cannot Change the order status which is not belong to you");
    }

    @Override
    public OrderResponseDto patchOrderById(OrderPatchDto orderPatchDto, String orderId) {
        Optional<Order> optionalOrder = orderRepository.findByOrderIdAndIsActiveTrue(orderId);
        if(optionalOrder.isPresent()){
            Order order = optionalOrder.get();
            if(!(order.getOrderStatus().equals(OrderStatus.COMPLETED))) {
                Product product = order.getProduct();
                if(orderPatchDto.orderStatus() != null)
                    order.setOrderStatus(orderPatchDto.orderStatus());
                if(orderPatchDto.quantity() != null && orderPatchDto.quantity() > 0 && product.getAvailableStock() >= orderPatchDto.quantity()) {
                    order.setQuantity(orderPatchDto.quantity());
                    order.setTotalAmount(orderPatchDto.quantity() * product.getPrice());
                }
                log.info("Partial update for the orderId: {}", orderId);
                return OrderMapper.toResponse(orderRepository.save(order));
            } else
                throw new OrderNotFoundException("You cannot update the order which is already Completed");
        } else
            throw new OrderNotFoundException("No order found with id: "+ orderId);
    }
}

