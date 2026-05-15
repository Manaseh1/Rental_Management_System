package com.Rentals.app.service;

import com.Rentals.app.model.Room;
import com.Rentals.app.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public Room createRoom(Room room) {
        if (room.getRoomId() == null) {
            throw new IllegalArgumentException("Room ID is required and must be manually provided");
        }
        if (roomRepository.existsById(room.getRoomId())) {
            throw new IllegalArgumentException("Room already exists with id " + room.getRoomId());
        }
        if (room.getStatus() == null || room.getStatus().isEmpty()) {
            room.setStatus("vacant");
        }
        return roomRepository.save(room);
    }

    public Optional<Room> updateRoom(Long id, Room room) {
        return roomRepository.findById(id)
                .map(existingRoom -> {
                    existingRoom.setRoomType(room.getRoomType());
                    existingRoom.setRoomDescription(room.getRoomDescription());
                    existingRoom.setStatus(room.getStatus());
                    existingRoom.setRoomPrice(room.getRoomPrice());
                    return roomRepository.save(existingRoom);
                });
    }

    public boolean deleteRoom(Long id) {
        return roomRepository.findById(id)
                .map(room -> {
                    roomRepository.delete(room);
                    return true;
                })
                .orElse(false);
    }

    public List<Room> searchByType(String type) {
        return roomRepository.findByRoomType(type);
    }

    public List<Room> searchByPrice(Double roomPrice) {
        return roomRepository.findByRoomPrice(roomPrice);
    }

    public List<Room> searchByPriceRange(Double minPrice, Double maxPrice) {
        return roomRepository.findByRoomPriceBetween(minPrice, maxPrice);
    }

    public List<Room> searchByStatus(String status) {
        return roomRepository.findByStatus(status);
    }

    public boolean roomExists(Long id) {
        return id != null && roomRepository.existsById(id);
    }
}
