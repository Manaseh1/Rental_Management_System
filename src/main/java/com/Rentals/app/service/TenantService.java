package com.Rentals.app.service;
import com.Rentals.app.model.Tenant;
import com.Rentals.app.model.Room;
import com.Rentals.app.repository.TenantRepository;
import com.Rentals.app.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TenantService {
    private final TenantRepository tenantRepository;
    private final RoomRepository roomRepository;

    public TenantService(TenantRepository tenantRepository, RoomRepository roomRepository) {
        this.tenantRepository = tenantRepository;
        this.roomRepository = roomRepository;
    }

    public Optional<Tenant> getTenantById(Long id) {
        return tenantRepository.findById(id);
    }

    public List<Tenant> getTenantsByName(String name) {
        return tenantRepository.findByName(name);
    }

    public List<Tenant> getTenantsByPhoneNumber(String phoneNumber) {
        return tenantRepository.findByPhoneNumber(phoneNumber);
    }

    public List<Tenant> getTenantsByEmail(String email) {
        return tenantRepository.findByEmail(email);
    }


    public List<Tenant> getTenantsByRoomId(Long roomId) {
        return tenantRepository.findByRoom_RoomId(roomId);
    }
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    public Tenant createTenant(Tenant tenant) {
        if (tenant.getRoom() != null) {
            Long roomId = tenant.getRoom().getRoomId();
            if (roomId == null) {
                throw new IllegalArgumentException("Room ID is required when assigning a room to a tenant.");
            }
            Room persistedRoom = roomRepository.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException("Room with ID " + roomId + " does not exist."));
            tenant.setRoom(persistedRoom);
        }

        Tenant savedTenant = tenantRepository.save(tenant);
        // Automatically set room status to occupied when tenant is assigned a room
        if (savedTenant.getRoom() != null) {
            Room room = savedTenant.getRoom();
            room.setStatus("occupied");
            roomRepository.save(room);
        }
        return savedTenant;
    }

    public Optional<Tenant> updateTenant(Long id, Tenant tenantUpdate) {
        return tenantRepository.findById(id).map(existing -> {
            // copy updatable fields - adjust as needed based on Tenant fields
            existing.setName(tenantUpdate.getName());
            existing.setPhoneNumber(tenantUpdate.getPhoneNumber());
            existing.setEmail(tenantUpdate.getEmail());
            
            // Handle room assignment and status update
            if (tenantUpdate.getRoom() != null) {
                Long newRoomId = tenantUpdate.getRoom().getRoomId();
                if (newRoomId == null) {
                    throw new IllegalArgumentException("Room ID is required when assigning a room.");
                }
                Room newRoom = roomRepository.findById(newRoomId)
                        .orElseThrow(() -> new IllegalArgumentException("Room with ID " + newRoomId + " does not exist."));

                Long existingRoomId = existing.getRoom() != null ? existing.getRoom().getRoomId() : null;
                if (!newRoom.getRoomId().equals(existingRoomId)) {
                    newRoom.setStatus("occupied");
                    roomRepository.save(newRoom);
                    existing.setRoom(newRoom);
                }
            }
            
            Tenant savedTenant = tenantRepository.save(existing);
            // Ensure room status is set to occupied after update
            if (savedTenant.getRoom() != null) {
                Room room = savedTenant.getRoom();
                room.setStatus("occupied");
                roomRepository.save(room);
            }
            return savedTenant;
        });
    }

    public boolean deleteTenant(Long id) {
        return tenantRepository.findById(id).map(tenant -> {
            // Get the room before deletion
            Long roomId = tenant.getRoom() != null ? tenant.getRoom().getRoomId() : null;
            
            tenantRepository.deleteById(id);
            
            // If tenant was assigned to a room, check if any other tenants are in that room
            if (roomId != null) {
                List<Tenant> remainingTenants = tenantRepository.findByRoom_RoomId(roomId);
                if (remainingTenants.isEmpty()) {
                    // If no tenants left in the room, set status back to vacant
                    Optional<Room> roomOpt = roomRepository.findById(roomId);
                    roomOpt.ifPresent(room -> {
                        room.setStatus("vacant");
                        roomRepository.save(room);
                    });
                }
            }
            return true;
        }).orElse(false);
    }

    public List<Tenant> searchTenants(String name, String phoneNumber, String email, Long roomId) {
        if (name != null && !name.isBlank()) {
            return tenantRepository.findByName(name);
        }
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            return tenantRepository.findByPhoneNumber(phoneNumber);
        }
        if (email != null && !email.isBlank()) {
            return tenantRepository.findByEmail(email);
        }
        if (roomId != null) {
            return tenantRepository.findByRoom_RoomId(roomId);
        }
        return tenantRepository.findAll();
    }
}
