package com.hotel.service;

import com.hotel.dto.LoginRequest;
import com.hotel.dto.RegisterRequest;
import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import com.hotel.security.JwtUtil;
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
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setCustomerName("John Doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setCountryCode("+91");
        registerRequest.setMobileNumber("9876543210");
        registerRequest.setAddress("123 Main Street, City");
        registerRequest.setUsername("johndoe");
        registerRequest.setPassword("Password@1");
        registerRequest.setConfirmPassword("Password@1");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUserId("USR123");
        testUser.setCustomerName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setUsername("johndoe");
        testUser.setPassword("encodedPassword");
        testUser.setRole(User.Role.CUSTOMER);
        testUser.setStatus(User.AccountStatus.ACTIVE);
        testUser.setLocked(false);
        testUser.setFailedLoginAttempts(0);
    }

    @Test
    void testRegisterSuccess() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByMobileNumber(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(testUser);

        Map<String, Object> result = authService.register(registerRequest);

        assertNotNull(result);
        assertEquals("USR123", result.get("userId"));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testRegisterDuplicateEmail() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        assertEquals("Email already registered", ex.getMessage());
    }

    @Test
    void testRegisterDuplicateUsername() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        assertEquals("Username already taken", ex.getMessage());
    }

    @Test
    void testRegisterPasswordMismatch() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByMobileNumber(any())).thenReturn(false);
        registerRequest.setConfirmPassword("WrongPassword");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        assertEquals("Passwords do not match", ex.getMessage());
    }

    @Test
    void testLoginSuccess() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername("johndoe");
        loginReq.setPassword("Password@1");

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password@1", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("johndoe", "CUSTOMER")).thenReturn("mockToken");
        when(userRepository.save(any())).thenReturn(testUser);

        Map<String, Object> result = authService.login(loginReq);

        assertNotNull(result);
        assertEquals("mockToken", result.get("token"));
        assertEquals("CUSTOMER", result.get("role"));
    }

    @Test
    void testLoginInvalidCredentials() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername("johndoe");
        loginReq.setPassword("WrongPassword");

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongPassword", "encodedPassword")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(testUser);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(loginReq));
        assertEquals("Invalid username or password", ex.getMessage());
    }

    @Test
    void testLoginLockedAccount() {
        testUser.setLocked(true);
        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername("johndoe");
        loginReq.setPassword("Password@1");

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(loginReq));
        assertTrue(ex.getMessage().contains("locked"));
    }

    @Test
    void testLoginUserNotFound() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername("unknown");
        loginReq.setPassword("pass");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(loginReq));
        assertEquals("Invalid username or password", ex.getMessage());
    }
}
