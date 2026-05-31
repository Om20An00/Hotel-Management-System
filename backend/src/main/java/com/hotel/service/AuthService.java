package com.hotel.service;

import com.hotel.dto.LoginRequest;
import com.hotel.dto.RegisterRequest;
import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import com.hotel.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already registered");
        if (userRepository.existsByUsername(req.getUsername()))
            throw new RuntimeException("Username already taken");
        if (userRepository.existsByMobileNumber(req.getMobileNumber()))
            throw new RuntimeException("Mobile number already registered");
        if (!req.getPassword().equals(req.getConfirmPassword()))
            throw new RuntimeException("Passwords do not match");

        User user = new User();
        user.setUserId("USR" + System.currentTimeMillis());
        user.setCustomerName(req.getCustomerName());
        user.setEmail(req.getEmail());
        user.setCountryCode(req.getCountryCode());
        user.setMobileNumber(req.getMobileNumber());
        user.setAddress(req.getAddress());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(User.Role.CUSTOMER);
        user.setStatus(User.AccountStatus.ACTIVE);
        userRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getUserId());
        result.put("customerName", user.getCustomerName());
        result.put("email", user.getEmail());
        return result;
    }

    public Map<String, Object> login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (user.isLocked())
            throw new RuntimeException("Your account is locked. Please contact support.");

        if (user.getStatus() == User.AccountStatus.INACTIVE)
            throw new RuntimeException("Your account is inactive. Please contact support.");

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= 5) {
                user.setLocked(true);
            }
            userRepository.save(user);
            throw new RuntimeException("Invalid username or password");
        }

        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("role", user.getRole().name());
        result.put("username", user.getUsername());
        result.put("customerName", user.getCustomerName());
        result.put("userId", user.getUserId());
        result.put("mustChangePassword", user.isMustChangePassword());
        return result;
    }
}
