package com.Rentals.app.repository;
import com.Rentals.app.model.Rentcharge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RentchargeRepository extends JpaRepository<Rentcharge,Long> {
    List<Rentcharge> findByTenantId(Long tenantId);
    List<Rentcharge> findByDueDate(String dueDate);
}
