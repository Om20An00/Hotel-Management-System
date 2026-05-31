package com.hotel.controller;

import com.hotel.dto.ApiResponse;
import com.hotel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/customer/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Profile", userService.getProfile(principal.getName())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/customer/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> data, Principal principal) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Your profile has been updated successfully.", userService.updateProfile(principal.getName(), data)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/admin/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query) {
        try {
            if (query != null && !query.isEmpty())
                return ResponseEntity.ok(ApiResponse.ok("Users", userService.searchCustomers(query, page, size)));
            return ResponseEntity.ok(ApiResponse.ok("Users", userService.getAllCustomers(page, size)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/admin/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> data) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("User created successfully", userService.createUser(data)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/admin/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, String> data) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("User updated", userService.updateUser(id, data)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/admin/users/{id}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("User status updated", userService.toggleStatus(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/admin/users/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Password reset successful", userService.resetPassword(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/admin/staff")
    public ResponseEntity<?> getStaff(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Staff", userService.getAllStaff(page, size)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
