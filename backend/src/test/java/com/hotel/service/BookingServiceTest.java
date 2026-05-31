package com.hotel.service;

import com.hotel.dto.BookingRequest;
import com.hotel.model.*;
import com.hotel.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private UserRepository userRepository;
    @Mock private BillRepository billRepository;

    @InjectMocks private BookingService bookingService;

    private User testUser;
    private Room testRoom;
    private BookingRequest bookingRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserId("USR001");
        testUser.setUsername("testuser");
        testUser.setCustomerName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole(User.Role.CUSTOMER);
        testUser.setStatus(User.AccountStatus.ACTIVE);

        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setRoomNumber("R101");
        testRoom.setRoomType(Room.RoomType.DELUXE);
        testRoom.setPricePerNight(new BigDecimal("2000"));
        testRoom.setMaxOccupancy(2);
        testRoom.setStatus(Room.RoomStatus.AVAILABLE);

        bookingRequest = new BookingRequest();
        bookingRequest.setRoomId(1L);
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(3));
        bookingRequest.setNumberOfAdults(2);
        bookingRequest.setNumberOfChildren(0);
        bookingRequest.setPaymentMethod("CREDIT_CARD");
    }

    @Test
    void testCreateBookingSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(roomRepository.findAllAvailableRooms(any(), any())).thenReturn(List.of(testRoom));
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Booking result = bookingService.createBooking("testuser", bookingRequest);

        assertNotNull(result);
        assertNotNull(result.getBookingId());
        assertEquals(Booking.BookingStatus.CONFIRMED, result.getStatus());
        assertEquals(Booking.PaymentStatus.PENDING, result.getPaymentStatus());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void testCreateBookingRoomNotAvailable() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(roomRepository.findAllAvailableRooms(any(), any())).thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> bookingService.createBooking("testuser", bookingRequest));
        assertEquals("Room is not available for selected dates", ex.getMessage());
    }

    @Test
    void testCreateBookingOccupiedRoom() {
        testRoom.setStatus(Room.RoomStatus.OCCUPIED);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> bookingService.createBooking("testuser", bookingRequest));
        assertEquals("Room is not available", ex.getMessage());
    }

    @Test
    void testCancelBookingSuccess() {
        Booking booking = new Booking();
        booking.setBookingId("BK001");
        booking.setUser(testUser);
        booking.setCheckInDate(LocalDate.now().plusDays(5));
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        when(bookingRepository.findByBookingId("BK001")).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Booking result = bookingService.cancelBooking("BK001", "testuser");

        assertEquals(Booking.BookingStatus.CANCELLED, result.getStatus());
    }

    @Test
    void testCancelCheckedInBookingBlocked() {
        Booking booking = new Booking();
        booking.setBookingId("BK001");
        booking.setUser(testUser);
        booking.setStatus(Booking.BookingStatus.CHECKED_IN);

        when(bookingRepository.findByBookingId("BK001")).thenReturn(Optional.of(booking));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> bookingService.cancelBooking("BK001", "testuser"));
        assertTrue(ex.getMessage().contains("checked in"));
    }

    @Test
    void testGetUpcomingBookings() {
        Booking b = new Booking();
        b.setBookingId("BK001");
        b.setUser(testUser);
        b.setCheckInDate(LocalDate.now().plusDays(2));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(bookingRepository.findUpcomingBookings(any(), any())).thenReturn(List.of(b));

        List<Booking> result = bookingService.getUpcomingBookings("testuser");

        assertEquals(1, result.size());
        assertEquals("BK001", result.get(0).getBookingId());
    }
}
