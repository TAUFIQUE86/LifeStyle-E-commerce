package com.ecom.dto;

import com.ecom.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String email;
    private Role role;
}
