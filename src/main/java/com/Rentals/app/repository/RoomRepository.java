package com.Rentals.app.repository;

import com.Rentals.app.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, String> {

    Optional<Room> findByRoomId(String roomId);

    List<Room> findByRoomType(String roomType);

    List<Room> findByRoomPrice(Double roomPrice);

    List<Room> findByRoomPriceBetween(Double minPrice, Double maxPrice);

    List<Room> findByStatus(String status);

    boolean existsByRoomId(String roomId);
}
