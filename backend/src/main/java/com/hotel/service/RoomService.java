package com.hotel.service;

import com.hotel.model.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public Room addRoom(Room room) {
        if (roomRepository.existsByRoomNumber(room.getRoomNumber()))
            throw new RuntimeException("Room number already exists");
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room updated) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        if (room.getStatus() == Room.RoomStatus.OCCUPIED)
            throw new RuntimeException("Rooms marked as Occupied cannot be updated");
        room.setRoomType(updated.getRoomType());
        room.setBedType(updated.getBedType());
        room.setPricePerNight(updated.getPricePerNight());
        room.setStatus(updated.getStatus());
        room.setAmenities(updated.getAmenities());
        room.setMaxOccupancy(updated.getMaxOccupancy());
        room.setDescription(updated.getDescription());
        room.setRoomSize(updated.getRoomSize());
        return roomRepository.save(room);
    }

    public Page<Room> getAllRooms(int page, int size, String sort, String direction) {
        Sort.Direction dir = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort));
        return roomRepository.findAll(pageable);
    }

    public Page<Room> searchRooms(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return roomRepository.searchRooms(query, pageable);
    }

    public List<Room> findAvailableRooms(String type, LocalDate checkIn, LocalDate checkOut) {
        Room.RoomType roomType = Room.RoomType.valueOf(type.toUpperCase());
        return roomRepository.findAvailableRooms(roomType, checkIn, checkOut);
    }

    public List<Room> findAllAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAllAvailableRooms(checkIn, checkOut);
    }

    public Room getById(Long id) {
        return roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public int bulkImport(MultipartFile file) {
        int count = 0;
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            boolean first = true;
            while ((line = reader.readNext()) != null) {
                if (first) { first = false; continue; }
                if (line.length < 6) continue;
                Room room = new Room();
                room.setRoomNumber(line[0].trim());
                room.setRoomType(Room.RoomType.valueOf(line[1].trim().toUpperCase()));
                room.setPricePerNight(new BigDecimal(line[2].trim()));
                room.setMaxOccupancy(Integer.parseInt(line[3].trim()));
                room.setStatus(Room.RoomStatus.valueOf(line[4].trim().toUpperCase()));
                room.setDescription(line[5].trim());
                if (!roomRepository.existsByRoomNumber(room.getRoomNumber())) {
                    roomRepository.save(room);
                    count++;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("CSV import failed: " + e.getMessage());
        }
        return count;
    }

    public long countAvailableRooms() {
        return roomRepository.findAll().stream()
                .filter(r -> r.getStatus() == Room.RoomStatus.AVAILABLE).count();
    }
}
