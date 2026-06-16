package com.Rentals.app.service;
import com.Rentals.app.model.Tenant;
import com.Rentals.app.model.Room;
import com.Rentals.app.repository.TenantRepository;
import com.Rentals.app.repository.RoomRepository;
import com.Rentals.app.repository.RentchargeRepository;
import com.Rentals.app.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TenantService {
    private final TenantRepository tenantRepository;
    private final RoomRepository roomRepository;
    private final RentchargeRepository rentchargeRepository;
    private final PaymentRepository paymentRepository;

    public TenantService(TenantRepository tenantRepository, RoomRepository roomRepository,
                         RentchargeRepository rentchargeRepository, PaymentRepository paymentRepository) {
        this.tenantRepository = tenantRepository;
        this.roomRepository = roomRepository;
        this.rentchargeRepository = rentchargeRepository;
        this.paymentRepository = paymentRepository;
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

    public List<Tenant> getTenantsByIdentificationNumber(String identificationNumber) {
        return tenantRepository.findByIdentificationNumber(identificationNumber);
    }


    public List<Tenant> getTenantsByRoomId(Long roomId) {
        return tenantRepository.findByRoom_RoomId(roomId);
    }
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    public Double calculateTenantBalance(Long tenantId) {
        java.util.List<com.Rentals.app.model.Rentcharge> rentcharges = 
            rentchargeRepository.findByTenantId(tenantId);
        java.util.List<com.Rentals.app.model.Payment> payments = 
            paymentRepository.findByTenantId(tenantId);
        
        Double totalCharges = rentcharges.stream()
            .mapToDouble(rc -> rc.getChargeAmount() != null ? rc.getChargeAmount() : 0.0)
            .sum();
        
        Double totalPayments = payments.stream()
            .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
            .sum();
        
        return totalCharges - totalPayments;
    }

    public void processOverdueCharges() {
        String today = java.time.LocalDate.now().toString();
        java.util.List<Tenant> allTenants = tenantRepository.findAll();
        
        for (Tenant tenant : allTenants) {
            java.util.List<com.Rentals.app.model.Rentcharge> overdueCharges = 
                rentchargeRepository.findByTenantId(tenant.getId()).stream()
                    .filter(rc -> rc.getDueDate().compareTo(today) < 0)
                    .toList();
            
            for (com.Rentals.app.model.Rentcharge charge : overdueCharges) {
                Double currentBalance = calculateTenantBalance(tenant.getId());
                charge.setChargeAmount(currentBalance + charge.getChargeAmount());
                rentchargeRepository.save(charge);
            }
        }
    }

    public Tenant createTenant(Tenant tenant) {
        if (tenant.getRoom() != null) {
            String roomId = tenant.getRoom().getRoomId();
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
            existing.setIdentificationNumber(tenantUpdate.getIdentificationNumber());

            // Handle room assignment and status update
            if (tenantUpdate.getRoom() != null) {
                String newRoomId = tenantUpdate.getRoom().getRoomId();
                if (newRoomId == null) {
                    throw new IllegalArgumentException("Room ID is required when assigning a room.");
                }
                Room newRoom = roomRepository.findById(newRoomId)
                        .orElseThrow(() -> new IllegalArgumentException("Room with ID " + newRoomId + " does not exist."));

                String existingRoomId = existing.getRoom() != null ? existing.getRoom().getRoomId() : null;
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

    @Transactional
    public boolean deleteTenant(Long id) {
        return tenantRepository.findById(id).map(tenant -> {
            String roomId = tenant.getRoom() != null ? tenant.getRoom().getRoomId() : null;

            if (roomId != null) {
                tenant.setRoom(null);
                tenantRepository.save(tenant);
            }

            rentchargeRepository.deleteAll(rentchargeRepository.findByTenantId(id));
            paymentRepository.deleteAll(paymentRepository.findByTenantId(id));
            tenantRepository.deleteById(id);

            if (roomId != null) {
                roomRepository.findById(roomId).ifPresent(room -> {
                    room.setTenant(null);
                    room.setStatus("vacant");
                    roomRepository.save(room);
                });
            }
            return true;
        }).orElse(false);
    }

    public List<Tenant> searchTenants(String name, String phoneNumber, String identificationNumber, Long roomId) {
        if (name != null && !name.isBlank()) {
            return tenantRepository.findByName(name);
        }
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            return tenantRepository.findByPhoneNumber(phoneNumber);
        }
        if (identificationNumber != null && !identificationNumber.isBlank()) {
            return tenantRepository.findByIdentificationNumber(identificationNumber);
        }
        if (roomId != null) {
            return tenantRepository.findByRoom_RoomId(roomId);
        }
        return tenantRepository.findAll();
    }
}
