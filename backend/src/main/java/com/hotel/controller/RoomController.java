package com.hotel.controller;

import com.hotel.dto.ApiResponse;
import com.hotel.model.Room;
import com.hotel.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/rooms/available")
    public ResponseEntity<?> getAvailable(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) String roomType) {
        try {
            List<Room> rooms = roomType != null
                    ? roomService.findAvailableRooms(roomType, checkIn, checkOut)
                    : roomService.findAllAvailableRooms(checkIn, checkOut);
            return ResponseEntity.ok(ApiResponse.ok("Available rooms", rooms));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Room", roomService.getById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/admin/rooms")
    public ResponseEntity<?> getAllRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "roomNumber") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String query) {
        try {
            if (query != null && !query.isEmpty())
                return ResponseEntity.ok(ApiResponse.ok("Rooms", roomService.searchRooms(query, page, size)));
            return ResponseEntity.ok(ApiResponse.ok("Rooms", roomService.getAllRooms(page, size, sort, direction)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/admin/rooms")
    public ResponseEntity<?> addRoom(@RequestBody Room room) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Room added successfully", roomService.addRoom(room)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/admin/rooms/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody Room room) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Room " + id + " details are updated successfully", roomService.updateRoom(id, room)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/admin/rooms/bulk-import")
    public ResponseEntity<?> bulkImport(@RequestParam("file") MultipartFile file) {
        try {
            int count = roomService.bulkImport(file);
            return ResponseEntity.ok(ApiResponse.ok("Bulk upload is successful. " + count + " rooms imported.", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
