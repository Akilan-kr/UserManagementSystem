package com.example.usermanagementsystem.usermanagementsystem.Mapper;

import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.ProductRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.PageResponse;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.ProductResponseDto;
import com.example.usermanagementsystem.usermanagementsystem.Entity.Product;
import org.springframework.data.domain.Page;

public class ProductMapper {
    public static Product toEntity(ProductRequestDto productRequestDto) {
        return Product.builder()
                .productName(productRequestDto.productName())
                .price(productRequestDto.price())
                .availableStock(productRequestDto.availableStock())
                .build();
    }

    public static ProductResponseDto toResponse(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .availableStock(product.getAvailableStock())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
