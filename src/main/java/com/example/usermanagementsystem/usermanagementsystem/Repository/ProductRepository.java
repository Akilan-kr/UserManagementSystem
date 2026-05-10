package com.example.usermanagementsystem.usermanagementsystem.Repository;

import com.example.usermanagementsystem.usermanagementsystem.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByIdAndIsActiveTrue(Integer id);
    Page<Product> findByAndIsActiveTrue(Pageable pageable, String search);
}
