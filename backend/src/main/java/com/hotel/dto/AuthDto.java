package com.hotel.dto;

public class AuthDto {

    public static class RegisterRequest {
        private String customerName;
        private String email;
        private String countryCode;
        private String mobileNumber;
        private String address;
        private String username;
        private String password;

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getCountryCode() { return countryCode; }
        public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

        public String getMobileNumber() { return mobileNumber; }
        public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String token;
        private String userId;
        private String username;
        private String customerName;
        private String role;
        private boolean mustChangePassword;

        public AuthResponse(String token, String userId, String username, String customerName, String role, boolean mustChangePassword) {
            this.token = token;
            this.userId = userId;
            this.username = username;
            this.customerName = customerName;
            this.role = role;
            this.mustChangePassword = mustChangePassword;
        }

        public String getToken() { return token; }
        public String getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getCustomerName() { return customerName; }
        public String getRole() { return role; }
        public boolean isMustChangePassword() { return mustChangePassword; }
    }

    public static class RegisterResponse {
        private String userId;
        private String customerName;
        private String email;
        private String message;

        public RegisterResponse(String userId, String customerName, String email, String message) {
            this.userId = userId;
            this.customerName = customerName;
            this.email = email;
            this.message = message;
        }

        public String getUserId() { return userId; }
        public String getCustomerName() { return customerName; }
        public String getEmail() { return email; }
        public String getMessage() { return message; }
    }

    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
