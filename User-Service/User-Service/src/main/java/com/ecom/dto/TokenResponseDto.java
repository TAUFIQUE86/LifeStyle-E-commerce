package com.ecom.dto;

public record TokenResponseDto(String accessToken, String tokenType, long expiresIn) {
}
