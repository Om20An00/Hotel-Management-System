package com.hotel.repository;

import com.hotel.model.Bill;
import com.hotel.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByBillId(String billId);
    Optional<Bill> findByBooking(com.hotel.model.Booking booking);

    @Query("SELECT b FROM Bill b WHERE " +
           "(LOWER(b.billId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.user.customerName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.user.userId) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Bill> searchBills(@Param("query") String query, Pageable pageable);

    Page<Bill> findByUser(User user, Pageable pageable);
    Page<Bill> findByPaymentStatus(Bill.PaymentStatus status, Pageable pageable);
}
