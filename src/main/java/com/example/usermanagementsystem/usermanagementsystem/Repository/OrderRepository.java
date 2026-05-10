package com.example.usermanagementsystem.usermanagementsystem.Repository;

import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.OrderResponseDto;
import com.example.usermanagementsystem.usermanagementsystem.Entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findByIsActive(Pageable pageable, boolean isActive);

    List<Order> findAllByUserIdAndIsActiveTrue(Integer id);

    Page<Order> findByAndIsActiveTrue(Pageable pageable, String search);

    Optional<Order> findByOrderIdAndIsActiveTrue(String orderId);

}

