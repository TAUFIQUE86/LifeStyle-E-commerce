package com.ecom.service;

import com.ecom.dto.UserRegistrationDto;
import com.ecom.dto.UserResponseDto;
import com.ecom.entity.Role;
import com.ecom.entity.User;
import com.ecom.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        String email = registrationDto.getEmail().trim().toLowerCase(Locale.ROOT);

        // 1. Validation (e.g., check if email exists)
        if (userRepository.existsByEmail(email)) {
            log.warn("Registration rejected because the email is already registered");
            throw new IllegalArgumentException("Email is already in use");
        }

        // 2. Map DTO to Entity
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        // Public registration must never grant elevated privileges.
        user.setRole(Role.USER);

        // 3. Save Entity
        User savedUser = userRepository.save(user);
        log.info("Registered user with id={}", savedUser.getId());

        // 4. Map Entity to Response DTO
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(savedUser.getId());
        responseDto.setEmail(savedUser.getEmail());
        responseDto.setRole(savedUser.getRole());

        return responseDto;
    }
}
