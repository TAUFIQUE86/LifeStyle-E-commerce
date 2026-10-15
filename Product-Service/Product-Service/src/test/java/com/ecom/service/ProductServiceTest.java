package com.ecom.service;

import com.ecom.dto.ProductRequestDto;
import com.ecom.dto.ProductResponseDto;
import com.ecom.entity.Product;
import com.ecom.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createsAndMapsProduct() {
        ProductRequestDto request = new ProductRequestDto("T-shirt", "Cotton T-shirt", 25.0, 10);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId("product-1");
            return product;
        });

        ProductResponseDto response = productService.createProduct(request, "seller-1");

        ArgumentCaptor<Product> savedProduct = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(savedProduct.capture());
        assertEquals("product-1", response.id());
        assertEquals("T-shirt", savedProduct.getValue().getName());
        assertEquals(25.0, response.price());
    }

    @Test
    void returnsAllProducts() {
        Product product = new Product("product-1", "T-shirt", "Cotton T-shirt", 25.0, 10, "seller-1");
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponseDto> products = productService.getAllProducts();

        assertEquals(1, products.size());
        assertEquals("product-1", products.get(0).id());
    }

    @Test
    void sellerCanUpdateOwnProductButNotAnotherSellersProduct() {
        Product product = new Product("product-1", "T-shirt", "Cotton T-shirt", 25.0, 10, "seller-1");
        ProductRequestDto update = new ProductRequestDto("Hoodie", "Warm hoodie", 45.0, 5);
        when(productRepository.findById("product-1")).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        ProductResponseDto updated = productService.updateProduct("product-1", update, "seller-1", false);

        assertEquals("Hoodie", updated.name());
        assertThrows(ProductAccessDeniedException.class,
                () -> productService.updateProduct("product-1", update, "seller-2", false));
    }

    @Test
    void adminCanDeleteAnyProduct() {
        Product product = new Product("product-1", "T-shirt", "Cotton T-shirt", 25.0, 10, "seller-1");
        when(productRepository.findById("product-1")).thenReturn(Optional.of(product));

        productService.deleteProduct("product-1", "admin-1", true);

        verify(productRepository).delete(product);
    }
}
