package com.Rentals.app.repository;

import com.Rentals.app.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    List<Tenant> findById(String id);

    List<Tenant> findByIdentificationNumber(String identificationNumber);

    List<Tenant> findByName(String name);

    List<Tenant> findByPhoneNumber(String phoneNumber);


    List<Tenant> findByRoom_RoomId(Long roomId);
}