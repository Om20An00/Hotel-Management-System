package com.hotel.controller;

import com.hotel.dto.*;
import com.hotel.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/customer/bookings")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest req, Principal principal) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Booking created", bookingService.createBooking(principal.getName(), req)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/customer/bookings/{bookingId}/pay")
    public ResponseEntity<?> pay(@PathVariable String bookingId, @RequestBody(required = false) PaymentRequest payReq, Principal principal) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Payment successful! Your booking is confirmed.", bookingService.processPayment(principal.getName(), bookingId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/customer/bookings/upcoming")
    public ResponseEntity<?> upcoming(Principal principal) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Upcoming bookings", bookingService.getUpcomingBookings(principal.getName())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/customer/bookings/past")
    public ResponseEntity<?> past(Principal principal) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Past bookings", bookingService.getPastBookings(principal.getName())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/customer/bookings/{bookingId}")
    public ResponseEntity<?> getBooking(@PathVariable String bookingId, Principal principal) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Booking", bookingService.getBookingByIdForUser(bookingId, principal.getName())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/customer/bookings/{bookingId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String bookingId, Principal principal) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Booking cancelled", bookingService.cancelBooking(bookingId, principal.getName())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/customer/bookings/{bookingId}/modify")
    public ResponseEntity<?> modify(@PathVariable String bookingId, @RequestBody BookingRequest req, Principal principal) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Your booking has been successfully modified.", bookingService.modifyBooking(bookingId, principal.getName(), req)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/admin/bookings")
    public ResponseEntity<?> adminGetAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query) {
        try {
            if (query != null && !query.isEmpty())
                return ResponseEntity.ok(ApiResponse.ok("Bookings", bookingService.searchBookings(query, page, size)));
            return ResponseEntity.ok(ApiResponse.ok("Bookings", bookingService.getAllBookings(page, size)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/admin/bookings/{bookingId}/cancel")
    public ResponseEntity<?> adminCancel(@PathVariable String bookingId) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Booking cancelled", bookingService.adminCancelBooking(bookingId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/admin/bookings/{bookingId}")
    public ResponseEntity<?> adminUpdate(@PathVariable String bookingId, @RequestBody BookingRequest req) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Booking updated", bookingService.adminUpdateBooking(bookingId, req)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/admin/bookings")
    public ResponseEntity<?> adminCreateBooking(@RequestBody BookingRequest req) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Booking created successfully", bookingService.adminCreateBooking(req)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/admin/bookings/stats")
    public ResponseEntity<?> stats() {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Stats", bookingService.getDashboardStats()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
