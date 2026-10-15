package com.ecom.restcontroller;
import com.ecom.dto.ProductRequestDto;
import com.ecom.dto.ProductResponseDto;
import com.ecom.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
@Slf4j
public class ProductRestController {


    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto productRequestDto,
                                                            Authentication authentication) {
        log.debug("Received product creation request");
        ProductResponseDto createdProduct = productService.createProduct(productRequestDto, authentication.getName());
        log.info("Product creation completed with id={}", createdProduct.id());
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        log.debug("Returning {} products through the API", products.size());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable String productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ProductResponseDto>> createProducts(@NotEmpty @RequestBody List<@Valid ProductRequestDto> productRequests,
                                                                    Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProducts(productRequests, authentication.getName()));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable String productId,
                                                             @Valid @RequestBody ProductRequestDto productRequestDto,
                                                             Authentication authentication) {
        return ResponseEntity.ok(productService.updateProduct(productId, productRequestDto, authentication.getName(), isAdmin(authentication)));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId, Authentication authentication) {
        productService.deleteProduct(productId, authentication.getName(), isAdmin(authentication));
        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
