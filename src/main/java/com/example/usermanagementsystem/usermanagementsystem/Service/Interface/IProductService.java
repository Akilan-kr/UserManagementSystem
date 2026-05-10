package com.example.usermanagementsystem.usermanagementsystem.Service.Interface;

import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.ProductPatchDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.ProductRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.PageResponse;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.ProductResponseDto;

public interface IProductService {
    ProductResponseDto addProduct(ProductRequestDto productRequestDto);

    ProductResponseDto getProductById(Integer id);

    PageResponse<ProductResponseDto> getAllProducts(int page, int size, boolean ascending, String sortBy, String name);

    ProductResponseDto patchOrderById(ProductPatchDto productPatchDto, Integer id);

    void deleteProductById(Integer id);
}
