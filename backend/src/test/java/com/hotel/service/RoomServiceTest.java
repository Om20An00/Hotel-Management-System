package com.hotel.service;

import com.hotel.model.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private RoomService roomService;

    private Room testRoom;

    @BeforeEach
    void setUp() {
        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setRoomNumber("R001");
        testRoom.setRoomType(Room.RoomType.DELUXE);
        testRoom.setPricePerNight(new BigDecimal("3000"));
        testRoom.setMaxOccupancy(2);
        testRoom.setStatus(Room.RoomStatus.AVAILABLE);
    }

    @Test
    void testAddRoomSuccess() {
        when(roomRepository.existsByRoomNumber("R001")).thenReturn(false);
        when(roomRepository.save(any())).thenReturn(testRoom);

        Room result = roomService.addRoom(testRoom);

        assertNotNull(result);
        assertEquals("R001", result.getRoomNumber());
        verify(roomRepository, times(1)).save(any());
    }

    @Test
    void testAddRoomDuplicateNumber() {
        when(roomRepository.existsByRoomNumber("R001")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> roomService.addRoom(testRoom));
        assertEquals("Room number already exists", ex.getMessage());
    }

    @Test
    void testUpdateRoomOccupiedBlocked() {
        testRoom.setStatus(Room.RoomStatus.OCCUPIED);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> roomService.updateRoom(1L, testRoom));
        assertEquals("Rooms marked as Occupied cannot be updated", ex.getMessage());
    }

    @Test
    void testUpdateRoomSuccess() {
        Room update = new Room();
        update.setRoomType(Room.RoomType.SUITE);
        update.setPricePerNight(new BigDecimal("5000"));
        update.setMaxOccupancy(3);
        update.setStatus(Room.RoomStatus.AVAILABLE);
        update.setBedType(Room.BedType.KING);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(roomRepository.save(any())).thenReturn(testRoom);

        Room result = roomService.updateRoom(1L, update);

        assertNotNull(result);
        verify(roomRepository, times(1)).save(any());
    }

    @Test
    void testGetByIdNotFound() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> roomService.getById(99L));
        assertEquals("Room not found", ex.getMessage());
    }
}
