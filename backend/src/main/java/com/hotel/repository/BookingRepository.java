package com.hotel.repository;

import com.hotel.model.Booking;
import com.hotel.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingId(String bookingId);
    List<Booking> findByUser(User user);

    Page<Booking> findByUser(User user, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.checkInDate >= :today AND b.status != 'CANCELLED'")
    List<Booking> findUpcomingBookings(@Param("user") User user, @Param("today") LocalDate today);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.checkOutDate < :today")
    List<Booking> findPastBookings(@Param("user") User user, @Param("today") LocalDate today);

    @Query("SELECT b FROM Booking b WHERE " +
           "(LOWER(b.bookingId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.user.customerName) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Booking> searchBookings(@Param("query") String query, Pageable pageable);

    boolean existsByUserAndRoomAndCheckInDateAndCheckOutDateAndStatusNot(
        User user, com.hotel.model.Room room, LocalDate checkIn, LocalDate checkOut, Booking.BookingStatus status);

    long countByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
    long countByStatus(Booking.BookingStatus status);
}
