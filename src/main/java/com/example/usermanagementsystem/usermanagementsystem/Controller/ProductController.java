package com.example.usermanagementsystem.usermanagementsystem.Controller;

import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.ProductPatchDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.ProductRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.ApiResponse;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.OrderResponseDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.PageResponse;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.ProductResponseDto;
import com.example.usermanagementsystem.usermanagementsystem.Entity.Product;
import com.example.usermanagementsystem.usermanagementsystem.Exception.OrderNotFoundException;
import com.example.usermanagementsystem.usermanagementsystem.Service.Interface.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ProductResponseDto>> addProduct(@RequestBody ProductRequestDto productRequestDto){
        log.info("/product/add API endpoint called");
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(productService.addProduct(productRequestDto), "Successfully Created", true));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProductById(@PathVariable Integer id){
        log.info("/product/get/id API endpoint called for id:({})", id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(productService.getProductById(id), "Successfully get the product", true));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponseDto>>> getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending,
            @RequestParam(defaultValue = "") String name){
        log.info("/product/getAllProducts API endpoint called");
        PageResponse<ProductResponseDto> pageProductResponse = productService.getAllProducts(page, size, ascending, sortBy, name);
        if(!(pageProductResponse.content().isEmpty()))
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(pageProductResponse, "Successfully got all the Product", true));
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(pageProductResponse, "No Products In the list", true));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(@PathVariable Integer id,@RequestBody ProductPatchDto productPatchDto){
        log.info("/update/id API endpoint called for the order id: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(productService.patchOrderById(productPatchDto, id), "Product Updated Successfully", true));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<?>> deleteProductById(@PathVariable Integer id){
        log.info("/deleteProduct/id API endpoint called for the Product id: {}", id);
        productService.deleteProductById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(null, "Product Deleted Successfully", true));
    }
}
