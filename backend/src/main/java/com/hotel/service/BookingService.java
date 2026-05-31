package com.hotel.service;

import com.hotel.dto.BookingRequest;
import com.hotel.model.*;
import com.hotel.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillRepository billRepository;

    public Booking createBooking(String username, BookingRequest req) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Room room = roomRepository.findById(req.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getStatus() != Room.RoomStatus.AVAILABLE)
            throw new RuntimeException("Room is not available");

        List<Room> available = roomRepository.findAllAvailableRooms(req.getCheckInDate(), req.getCheckOutDate());
        boolean roomAvailable = available.stream().anyMatch(r -> r.getId().equals(room.getId()));
        if (!roomAvailable)
            throw new RuntimeException("Room is not available for selected dates");

        long nights = ChronoUnit.DAYS.between(req.getCheckInDate(), req.getCheckOutDate());
        BigDecimal base = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        BigDecimal tax = base.multiply(new BigDecimal("0.18"));
        BigDecimal total = base.add(tax);

        Booking booking = new Booking();
        booking.setBookingId("BK" + System.currentTimeMillis());
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(req.getCheckInDate());
        booking.setCheckOutDate(req.getCheckOutDate());
        booking.setNumberOfAdults(req.getNumberOfAdults());
        booking.setNumberOfChildren(req.getNumberOfChildren());
        booking.setSpecialRequests(req.getSpecialRequests());
        booking.setPaymentMethod(req.getPaymentMethod());
        booking.setBasePrice(base);
        booking.setTaxAmount(tax);
        booking.setTotalAmount(total);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPaymentStatus(Booking.PaymentStatus.PENDING);
        return bookingRepository.save(booking);
    }

    public Booking processPayment(String username, String bookingId) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (!booking.getUser().getUsername().equals(username))
            throw new RuntimeException("Unauthorized");
        booking.setPaymentStatus(Booking.PaymentStatus.PAID);
        booking.setTransactionId("TXN" + System.currentTimeMillis());
        Booking saved = bookingRepository.save(booking);

        Bill bill = new Bill();
        bill.setBillId("BILL" + System.currentTimeMillis());
        bill.setUser(booking.getUser());
        bill.setBooking(booking);
        bill.setRoomCharges(booking.getBasePrice());
        bill.setTaxAmount(booking.getTaxAmount());
        bill.setServiceCharges(BigDecimal.ZERO);
        bill.setAdditionalFees(BigDecimal.ZERO);
        bill.setDiscount(BigDecimal.ZERO);
        bill.setTotalAmount(booking.getTotalAmount());
        bill.setPaymentStatus(Bill.PaymentStatus.PAID);
        billRepository.save(bill);

        return saved;
    }

    public List<Booking> getUpcomingBookings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookingRepository.findUpcomingBookings(user, LocalDate.now());
    }

    public List<Booking> getPastBookings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookingRepository.findPastBookings(user, LocalDate.now());
    }

    public Booking getBookingByIdForUser(String bookingId, String username) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (!booking.getUser().getUsername().equals(username))
            throw new RuntimeException("Unauthorized");
        return booking;
    }

    public Booking cancelBooking(String bookingId, String username) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (!booking.getUser().getUsername().equals(username))
            throw new RuntimeException("Unauthorized");
        if (booking.getStatus() == Booking.BookingStatus.CHECKED_IN)
            throw new RuntimeException("Cannot cancel a booking that is already checked in");
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED)
            throw new RuntimeException("Booking is already cancelled");
        long hoursToCheckin = ChronoUnit.HOURS.between(java.time.LocalDateTime.now(),
                booking.getCheckInDate().atStartOfDay());
        if (hoursToCheckin < 0)
            throw new RuntimeException("This booking cannot be canceled as it is past the allowed cancellation window");
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    public Booking modifyBooking(String bookingId, String username, BookingRequest req) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (!booking.getUser().getUsername().equals(username))
            throw new RuntimeException("Unauthorized");
        long hoursToCheckin = ChronoUnit.HOURS.between(java.time.LocalDateTime.now(),
                booking.getCheckInDate().atStartOfDay());
        if (hoursToCheckin < 24)
            throw new RuntimeException("Modifications are not allowed within 24 hours of check-in. Please contact support.");

        Room room = roomRepository.findById(req.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        long nights = ChronoUnit.DAYS.between(req.getCheckInDate(), req.getCheckOutDate());
        BigDecimal base = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        BigDecimal tax = base.multiply(new BigDecimal("0.18"));
        BigDecimal total = base.add(tax);

        booking.setRoom(room);
        booking.setCheckInDate(req.getCheckInDate());
        booking.setCheckOutDate(req.getCheckOutDate());
        booking.setNumberOfAdults(req.getNumberOfAdults());
        booking.setNumberOfChildren(req.getNumberOfChildren());
        booking.setBasePrice(base);
        booking.setTaxAmount(tax);
        booking.setTotalAmount(total);
        return bookingRepository.save(booking);
    }

    public Page<Booking> getAllBookings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return bookingRepository.findAll(pageable);
    }

    public Page<Booking> searchBookings(String query, int page, int size) {
        return bookingRepository.searchBookings(query, PageRequest.of(page, size));
    }

    public Booking adminCancelBooking(String bookingId) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (booking.getStatus() == Booking.BookingStatus.CHECKED_IN ||
            booking.getStatus() == Booking.BookingStatus.CHECKED_OUT)
            throw new RuntimeException("Cannot cancel this booking");
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    public Booking adminUpdateBooking(String bookingId, BookingRequest req) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setCheckInDate(req.getCheckInDate());
        booking.setCheckOutDate(req.getCheckOutDate());
        booking.setNumberOfAdults(req.getNumberOfAdults());
        booking.setNumberOfChildren(req.getNumberOfChildren());
        return bookingRepository.save(booking);
    }

    public Booking adminCreateBooking(BookingRequest req) {
        User user = userRepository.findByUserId(req.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Room room = roomRepository.findById(req.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        List<Room> available = roomRepository.findAllAvailableRooms(req.getCheckInDate(), req.getCheckOutDate());
        boolean roomAvailable = available.stream().anyMatch(r -> r.getId().equals(room.getId()));
        if (!roomAvailable)
            throw new RuntimeException("Room is not available for selected dates");
        long nights = ChronoUnit.DAYS.between(req.getCheckInDate(), req.getCheckOutDate());
        BigDecimal base = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        BigDecimal tax = base.multiply(new BigDecimal("0.18"));
        BigDecimal total = base.add(tax);
        Booking booking = new Booking();
        booking.setBookingId("BK" + System.currentTimeMillis());
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(req.getCheckInDate());
        booking.setCheckOutDate(req.getCheckOutDate());
        booking.setNumberOfAdults(req.getNumberOfAdults());
        booking.setNumberOfChildren(req.getNumberOfChildren());
        booking.setPaymentMethod(req.getPaymentMethod());
        booking.setBasePrice(base);
        booking.setTaxAmount(tax);
        booking.setTotalAmount(total);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPaymentStatus(Booking.PaymentStatus.PENDING);
        return bookingRepository.save(booking);
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        java.time.LocalDateTime today = java.time.LocalDateTime.now();
        stats.put("dailyBookings", bookingRepository.countByCreatedAtBetween(today.toLocalDate().atStartOfDay(), today));
        stats.put("weeklyBookings", bookingRepository.countByCreatedAtBetween(today.minusDays(7), today));
        stats.put("monthlyBookings", bookingRepository.countByCreatedAtBetween(today.minusDays(30), today));
        stats.put("confirmedBookings", bookingRepository.countByStatus(Booking.BookingStatus.CONFIRMED));
        stats.put("totalBookings", bookingRepository.count());
        return stats;
    }
}
