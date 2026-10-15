package com.ecom.service;

import com.ecom.dto.UserRegistrationDto;
import com.ecom.dto.UserResponseDto;
import com.ecom.entity.Role;
import com.ecom.entity.User;
import com.ecom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void registersNormalizedEmailWithHashedPasswordAndUserRole() {
        UserRegistrationDto request = new UserRegistrationDto();
        request.setEmail("  CUSTOMER@EXAMPLE.COM ");
        request.setPassword("secure-password");

        when(userRepository.existsByEmail("customer@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(42L);
            return user;
        });

        UserResponseDto response = userService.registerUser(request);

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedUser.capture());
        assertEquals(42L, response.getId());
        assertEquals("customer@example.com", savedUser.getValue().getEmail());
        assertEquals(Role.USER, savedUser.getValue().getRole());
        assertTrue(passwordEncoder.matches("secure-password", savedUser.getValue().getPassword()));
    }

    @Test
    void rejectsAlreadyRegisteredEmail() {
        UserRegistrationDto request = new UserRegistrationDto();
        request.setEmail("customer@example.com");
        request.setPassword("secure-password");
        when(userRepository.existsByEmail("customer@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(request));
    }
}
