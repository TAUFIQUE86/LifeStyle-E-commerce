package com.ecom.restcontroller;

import com.ecom.dto.ProductResponseDto;
import com.ecom.exception.ApiExceptionHandler;
import com.ecom.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void createsProductForValidRequest() throws Exception {
        when(productService.createProduct(any(), any()))
                .thenReturn(new ProductResponseDto("product-1", "T-shirt", "Cotton T-shirt", 25.0, 10));

        mockMvc.perform(post("/api/products")
                        .with(user("seller-1").roles("SELLER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"T-shirt\",\"description\":\"Cotton T-shirt\",\"price\":25.0,\"stockQuantity\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("product-1"));
    }

    @Test
    void returnsAllProducts() throws Exception {
        when(productService.getAllProducts())
                .thenReturn(List.of(new ProductResponseDto("product-1", "T-shirt", "Cotton T-shirt", 25.0, 10)));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("T-shirt"));
    }

    @Test
    void returnsOneProduct() throws Exception {
        when(productService.getProduct("product-1"))
                .thenReturn(new ProductResponseDto("product-1", "T-shirt", "Cotton T-shirt", 25.0, 10));

        mockMvc.perform(get("/api/products/product-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("product-1"));
    }

    @Test
    void sellerCanUpdateAndDeleteProduct() throws Exception {
        when(productService.updateProduct(eq("product-1"), any(), eq("seller-1"), eq(false)))
                .thenReturn(new ProductResponseDto("product-1", "Hoodie", "Warm hoodie", 45.0, 5));

        mockMvc.perform(put("/api/products/product-1")
                        .with(user("seller-1").roles("SELLER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Hoodie\",\"description\":\"Warm hoodie\",\"price\":45.0,\"stockQuantity\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hoodie"));

        mockMvc.perform(delete("/api/products/product-1")
                        .with(user("seller-1").roles("SELLER")))
                .andExpect(status().isNoContent());
        verify(productService).deleteProduct("product-1", "seller-1", false);
    }

    @Test
    void rejectsInvalidProductBeforeCallingService() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"description\":\"\",\"price\":-1,\"stockQuantity\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));

        verifyNoInteractions(productService);
    }
}
