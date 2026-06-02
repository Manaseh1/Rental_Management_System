package com.Rentals.app.repository;

import com.Rentals.app.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findById(Long id);

    List<Tenant> findByName(String name);

    List<Tenant> findByPhoneNumber(String phoneNumber);

    List<Tenant> findByEmail(String email);

    List<Tenant> findByRoom_RoomId(Long roomId);
}