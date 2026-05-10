package com.example.usermanagementsystem.usermanagementsystem.Service.Implementation;

import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.ProductPatchDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.ProductRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.PageResponse;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.ProductResponseDto;
import com.example.usermanagementsystem.usermanagementsystem.Entity.Order;
import com.example.usermanagementsystem.usermanagementsystem.Entity.Product;
import com.example.usermanagementsystem.usermanagementsystem.Enums.OrderStatus;
import com.example.usermanagementsystem.usermanagementsystem.Exception.OrderNotFoundException;
import com.example.usermanagementsystem.usermanagementsystem.Exception.ProductNotFoundException;
import com.example.usermanagementsystem.usermanagementsystem.Mapper.OrderMapper;
import com.example.usermanagementsystem.usermanagementsystem.Mapper.PageMapper;
import com.example.usermanagementsystem.usermanagementsystem.Mapper.ProductMapper;
import com.example.usermanagementsystem.usermanagementsystem.Repository.ProductRepository;
import com.example.usermanagementsystem.usermanagementsystem.Service.Interface.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ProductService implements IProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public ProductResponseDto addProduct(ProductRequestDto productRequestDto) {
        log.info("Adding product to the db");
        Product product = ProductMapper.toEntity(productRequestDto);
        product.setIsActive(true);
        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Cacheable(value = "products", key = "#id")
    @Override
    public ProductResponseDto getProductById(Integer id) {
        log.info("Getting product with id:({})", id);
        return ProductMapper.toResponse(productRepository.findById(id).orElseThrow(()-> new ProductNotFoundException("There is no Product with id:"+id)));
    }

    @Cacheable(value = "allProducts" , key = "#page + '_' + #size +  '_' + #ascending + '_' + #sortBy + '_' + #search" )
    @Override
    public PageResponse<ProductResponseDto> getAllProducts(int page, int size, boolean ascending, String sortBy, String search) {
        log.info("Getting All Product");
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of((page <= 0) ? 0 : page - 1, size <= 0 ? 5 : size , sort);
        Page<Product> productPage = productRepository.findByAndIsActiveTrue(pageable, search);
        return PageMapper.toPageResponse(productPage.map(ProductMapper::toResponse));
    }

    @CachePut(value = "products", key = "#id")
    @Override
    public ProductResponseDto patchOrderById(ProductPatchDto productPatchDto, Integer id) {
        Product product = productRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> new ProductNotFoundException("There is no Product with id: "+ id));
            if (productPatchDto.productName() != null)
                product.setProductName(productPatchDto.productName());
            if (productPatchDto.price() != null)
                product.setPrice(productPatchDto.price());
            if (productPatchDto.availableStock() != null)
                product.setAvailableStock(productPatchDto.availableStock());
            log.info("Partial update for the ProductId: {}", id);
            return ProductMapper.toResponse(productRepository.save(product));
    }

    @CacheEvict(value = "products", key = "#id")
    @Override
    public void deleteProductById(Integer id) {
        Product product = productRepository.findByIdAndIsActiveTrue(id).orElseThrow(()-> new ProductNotFoundException("There is no product with id: "+id));
        log.info("Deleted Product with product id: {}",id);
        product.setIsActive(false);
        productRepository.save(product);
    }
}
