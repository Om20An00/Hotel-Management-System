package com.hotel.repository;

import com.hotel.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomNumber(String roomNumber);
    boolean existsByRoomNumber(String roomNumber);

    @Query("SELECT r FROM Room r WHERE r.roomType = :type AND r.status = 'AVAILABLE' AND r.id NOT IN " +
           "(SELECT b.room.id FROM Booking b WHERE b.status != 'CANCELLED' AND " +
           "b.checkInDate < :checkOut AND b.checkOutDate > :checkIn)")
    List<Room> findAvailableRooms(@Param("type") Room.RoomType type,
                                   @Param("checkIn") LocalDate checkIn,
                                   @Param("checkOut") LocalDate checkOut);

    @Query("SELECT r FROM Room r WHERE r.id NOT IN " +
           "(SELECT b.room.id FROM Booking b WHERE b.status != 'CANCELLED' AND " +
           "b.checkInDate < :checkOut AND b.checkOutDate > :checkIn)")
    List<Room> findAllAvailableRooms(@Param("checkIn") LocalDate checkIn,
                                      @Param("checkOut") LocalDate checkOut);

    @Query("SELECT r FROM Room r WHERE " +
           "(LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "CAST(r.roomType AS string) LIKE UPPER(CONCAT('%', :query, '%')))")
    Page<Room> searchRooms(@Param("query") String query, Pageable pageable);

    Page<Room> findByRoomType(Room.RoomType roomType, Pageable pageable);
    Page<Room> findByStatus(Room.RoomStatus status, Pageable pageable);
}
