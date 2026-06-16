package com.Rentals.app.service;
import com.Rentals.app.model.Rentcharge;
import com.Rentals.app.model.Tenant;
import com.Rentals.app.repository.RentchargeRepository;
import com.Rentals.app.repository.TenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RentchargeService {
    private final RentchargeRepository rentchargeRepository;
    private final TenantRepository tenantRepository;

    public RentchargeService(RentchargeRepository rentchargeRepository, TenantRepository tenantRepository) {
        this.rentchargeRepository = rentchargeRepository;
        this.tenantRepository = tenantRepository;
    }

    public Optional<Rentcharge> getRentchargeById(Long id) {
        return rentchargeRepository.findById(id);
    }

    public Rentcharge createRentcharge(Rentcharge rentcharge) {
        // Validate tenant exists and has a room assigned
        if (rentcharge.getTenant() == null || rentcharge.getTenant().getId() == null) {
            throw new IllegalArgumentException("Tenant is required.");
        }
        
        Optional<Tenant> tenantOpt = tenantRepository.findById(rentcharge.getTenant().getId());
        if (tenantOpt.isEmpty()) {
            throw new IllegalArgumentException("Tenant with ID " + rentcharge.getTenant().getId() + " does not exist.");
        }
        
        Tenant tenant = tenantOpt.get();
        if (tenant.getRoom() == null) {
            throw new IllegalArgumentException("Tenant must be assigned to a room before creating a rent charge.");
        }

        rentcharge.setTenant(tenant);
        // Only set default charge amount if not provided by user
        if (rentcharge.getChargeAmount() == null) {
            rentcharge.setChargeAmount(tenant.getRoom().getRoomPrice());
        }
        return rentchargeRepository.save(rentcharge);
    }

    public Optional<Rentcharge> updateRentcharge(Long id, Rentcharge rentchargeUpdate) {
        return rentchargeRepository.findById(id).map(existing -> {
            if (rentchargeUpdate.getTenant() != null) {
                Optional<Tenant> tenantOpt = tenantRepository.findById(rentchargeUpdate.getTenant().getId());
                if (tenantOpt.isEmpty()) {
                    throw new IllegalArgumentException("Tenant with ID " + rentchargeUpdate.getTenant().getId() + " does not exist.");
                }
                Tenant tenant = tenantOpt.get();
                existing.setTenant(tenant);
                if (tenant.getRoom() == null) {
                    throw new IllegalArgumentException("Tenant must be assigned to a room before creating a rent charge.");
                }
            }
            if (rentchargeUpdate.getChargeAmount() != null) {
                existing.setChargeAmount(rentchargeUpdate.getChargeAmount());
            }
            if (rentchargeUpdate.getDueDate() != null) {
                existing.setDueDate(rentchargeUpdate.getDueDate());
            }
            return rentchargeRepository.save(existing);
        });
    }

    public void deleteRentcharge(Long id) {
        rentchargeRepository.deleteById(id);
    }

    public List<Rentcharge> getAllRentcharges() {
        return rentchargeRepository.findAll();
    }

    public List<Rentcharge> searchRentcharges(Long tenantId, String dueDate) {
        if (tenantId != null && dueDate != null) {
            return rentchargeRepository.findByTenantId(tenantId).stream()
                    .filter(rc -> rc.getDueDate().equals(dueDate))
                    .toList();
        } else if (tenantId != null) {
            return rentchargeRepository.findByTenantId(tenantId);
        } else if (dueDate != null) {
            return rentchargeRepository.findByDueDate(dueDate);
        } else {
            return rentchargeRepository.findAll();
        }
    }
}

