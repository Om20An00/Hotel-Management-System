package com.hotel.service;

import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserId("USR001");
        testUser.setUsername("testuser");
        testUser.setCustomerName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setMobileNumber("9876543210");
        testUser.setCountryCode("+91");
        testUser.setAddress("123 Test Street");
        testUser.setRole(User.Role.CUSTOMER);
        testUser.setStatus(User.AccountStatus.ACTIVE);
    }

    @Test
    void testGetProfileSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User result = userService.getProfile("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testGetProfileUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getProfile("unknown"));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testUpdateProfileSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Map<String, String> data = Map.of("customerName", "Updated Name", "email", "updated@example.com");
        User result = userService.updateProfile("testuser", data);

        assertNotNull(result);
        assertEquals("Updated Name", result.getCustomerName());
        assertEquals("updated@example.com", result.getEmail());
    }

    @Test
    void testToggleStatusDeactivate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User result = userService.toggleStatus(1L);

        assertEquals(User.AccountStatus.INACTIVE, result.getStatus());
    }

    @Test
    void testToggleStatusReactivate() {
        testUser.setStatus(User.AccountStatus.INACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User result = userService.toggleStatus(1L);

        assertEquals(User.AccountStatus.ACTIVE, result.getStatus());
    }

    @Test
    void testResetPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(any())).thenReturn("newEncodedPassword");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User result = userService.resetPassword(1L);

        assertTrue(result.isMustChangePassword());
        assertFalse(result.isLocked());
        assertEquals(0, result.getFailedLoginAttempts());
    }
}
