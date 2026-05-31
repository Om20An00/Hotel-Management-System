package com.hotel.service;

import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getProfile(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateProfile(String username, Map<String, String> data) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (data.containsKey("customerName")) user.setCustomerName(data.get("customerName"));
        if (data.containsKey("email")) user.setEmail(data.get("email"));
        if (data.containsKey("mobileNumber")) user.setMobileNumber(data.get("mobileNumber"));
        if (data.containsKey("countryCode")) user.setCountryCode(data.get("countryCode"));
        if (data.containsKey("address")) user.setAddress(data.get("address"));
        return userRepository.save(user);
    }

    public Page<User> getAllCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "customerName"));
        return userRepository.findByRole(User.Role.CUSTOMER, pageable);
    }

    public Page<User> searchCustomers(String query, int page, int size) {
        return userRepository.searchCustomers(query, PageRequest.of(page, size));
    }

    public User createUser(Map<String, String> data) {
        User user = new User();
        user.setUserId("USR" + System.currentTimeMillis());
        user.setCustomerName(data.get("customerName"));
        user.setEmail(data.get("email"));
        user.setUsername(data.get("username"));
        String tempPassword = "Hotel@" + System.currentTimeMillis() % 10000;
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setRole(User.Role.valueOf(data.getOrDefault("role", "CUSTOMER").toUpperCase()));
        user.setStatus(User.AccountStatus.ACTIVE);
        user.setMustChangePassword(true);
        User saved = userRepository.save(user);
        saved.setAddress(tempPassword);
        return saved;
    }

    public User updateUser(Long id, Map<String, String> data) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (data.containsKey("role")) user.setRole(User.Role.valueOf(data.get("role").toUpperCase()));
        if (data.containsKey("email")) user.setEmail(data.get("email"));
        if (data.containsKey("mobileNumber")) user.setMobileNumber(data.get("mobileNumber"));
        return userRepository.save(user);
    }

    public User toggleStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getStatus() == User.AccountStatus.ACTIVE) {
            user.setStatus(User.AccountStatus.INACTIVE);
        } else {
            user.setStatus(User.AccountStatus.ACTIVE);
        }
        return userRepository.save(user);
    }

    public User resetPassword(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String tempPassword = "Hotel@" + System.currentTimeMillis() % 10000;
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setMustChangePassword(true);
        user.setLocked(false);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
        user.setAddress(tempPassword);
        return user;
    }

    public Page<User> getAllStaff(int page, int size) {
        return userRepository.findByRole(User.Role.STAFF, PageRequest.of(page, size));
    }
}
