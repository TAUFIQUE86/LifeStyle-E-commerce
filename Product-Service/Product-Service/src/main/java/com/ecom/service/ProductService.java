package com.ecom.service;


import com.ecom.dto.ProductRequestDto;
import com.ecom.dto.ProductResponseDto;
import com.ecom.entity.Product;
import com.ecom.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {


    private final ProductRepository productRepository;

    public ProductResponseDto createProduct(ProductRequestDto requestDto, String sellerId) {
        Product product = new Product();
        // Use the record's accessor methods (no 'get' prefix)
        product.setName(requestDto.name());
        product.setDescription(requestDto.description());
        product.setPrice(requestDto.price());
        product.setStockQuantity(requestDto.stockQuantity());
        product.setSellerId(sellerId);

        Product savedProduct = productRepository.save(product);
        log.info("Created product with id={}", savedProduct.getId());
        return mapToResponseDto(savedProduct);
    }

    public List<ProductResponseDto> getAllProducts() {
        List<ProductResponseDto> products = productRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
        log.debug("Returning {} products", products.size());
        return products;
    }

    public ProductResponseDto getProduct(String productId) {
        return mapToResponseDto(findProduct(productId));
    }

    public List<ProductResponseDto> createProducts(List<ProductRequestDto> requests, String sellerId) {
        return requests.stream()
                .map(request -> createProduct(request, sellerId))
                .toList();
    }

    public ProductResponseDto updateProduct(String productId, ProductRequestDto requestDto, String requesterId, boolean isAdmin) {
        Product product = findProduct(productId);
        verifyOwnerOrAdmin(product, requesterId, isAdmin);
        product.setName(requestDto.name());
        product.setDescription(requestDto.description());
        product.setPrice(requestDto.price());
        product.setStockQuantity(requestDto.stockQuantity());
        Product savedProduct = productRepository.save(product);
        log.info("Updated product with id={}", savedProduct.getId());
        return mapToResponseDto(savedProduct);
    }

    public void deleteProduct(String productId, String requesterId, boolean isAdmin) {
        Product product = findProduct(productId);
        verifyOwnerOrAdmin(product, requesterId, isAdmin);
        productRepository.delete(product);
        log.info("Deleted product with id={}", productId);
    }

    private Product findProduct(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private void verifyOwnerOrAdmin(Product product, String requesterId, boolean isAdmin) {
        if (!isAdmin && !requesterId.equals(product.getSellerId())) {
            throw new ProductAccessDeniedException();
        }
    }

    // Helper method for mapping
    private ProductResponseDto mapToResponseDto(Product product) {
        // Instantiate the record using its automatically generated constructor
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity()
        );
    }
}
