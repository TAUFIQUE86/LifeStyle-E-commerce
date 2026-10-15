package com.ecom.restcontroller;

import com.ecom.dto.UserRegistrationDto;
import com.ecom.dto.UserResponseDto;

import com.ecom.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Slf4j
public class UserController {


    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRegistrationDto dto) {
        log.debug("Received user registration request");
        UserResponseDto createdUser = userService.registerUser(dto);
        log.info("User registration completed with id={}", createdUser.getId());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }


}
