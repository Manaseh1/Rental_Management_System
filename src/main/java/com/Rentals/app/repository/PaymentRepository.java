package com.Rentals.app.repository;

import com.Rentals.app.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByTenantId(Long tenantId);
    List<Payment> findByTenantIdOrderByPaymentDateDesc(Long tenantId);
}