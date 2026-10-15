package com.ecom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;





public record ProductRequestDto(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @Positive Double price,
        @NotNull @PositiveOrZero Integer stockQuantity
) {
}
