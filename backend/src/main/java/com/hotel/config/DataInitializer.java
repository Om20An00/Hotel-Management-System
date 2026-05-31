package com.hotel.config;

import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUserId("USR-ADMIN-001");
            admin.setCustomerName("Hotel Admin");
            admin.setEmail("admin@hotel.com");
            admin.setCountryCode("+91");
            admin.setMobileNumber("9000000001");
            admin.setAddress("Hotel Main Office");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("Admin@1234"));
            admin.setRole(User.Role.ADMIN);
            admin.setStatus(User.AccountStatus.ACTIVE);
            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("staff1")) {
            User staff = new User();
            staff.setUserId("USR-STAFF-001");
            staff.setCustomerName("Hotel Staff");
            staff.setEmail("staff@hotel.com");
            staff.setCountryCode("+91");
            staff.setMobileNumber("9000000002");
            staff.setAddress("Hotel Staff Office");
            staff.setUsername("staff1");
            staff.setPassword(passwordEncoder.encode("Staff@1234"));
            staff.setRole(User.Role.STAFF);
            staff.setStatus(User.AccountStatus.ACTIVE);
            userRepository.save(staff);
        }
    }
}
