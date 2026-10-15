package com.ecom.restcontroller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ProductApiIT {

    @Container
    private static final MongoDBContainer MONGODB = new MongoDBContainer("mongo:8.0");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGODB::getConnectionString);
        registry.add("eureka.client.enabled", () -> false);
        registry.add("app.jwt.secret", () -> "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "seller-1", roles = "SELLER")
    void createsThenRetrievesProduct() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"T-shirt\",\"description\":\"Cotton T-shirt\",\"price\":25.0,\"stockQuantity\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("T-shirt"));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockQuantity").value(10));
    }
}
