package com.ecom.dto;

public record ProductResponseDto(String id,
                                 String name,
                                 String description,
                                 Double price,
                                 Integer stockQuantity)
{
}
