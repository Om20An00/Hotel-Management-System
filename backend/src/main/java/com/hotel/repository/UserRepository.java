package com.hotel.repository;

import com.hotel.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String userId);
    Optional<User> findByMobileNumber(String mobileNumber);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByMobileNumber(String mobileNumber);

    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' AND " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.customerName) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<User> searchCustomers(@Param("query") String query, Pageable pageable);

    Page<User> findByRole(User.Role role, Pageable pageable);

    Page<User> findByRoleAndStatus(User.Role role, User.AccountStatus status, Pageable pageable);
}
