package com.Rentals.app.service;

import com.Rentals.app.model.Payment;
import com.Rentals.app.model.Tenant;
import com.Rentals.app.repository.PaymentRepository;
import com.Rentals.app.repository.TenantRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TenantRepository tenantRepository;

    public PaymentService(PaymentRepository paymentRepository, TenantRepository tenantRepository) {
        this.paymentRepository = paymentRepository;
        this.tenantRepository = tenantRepository;
    }

    // Get all payments for a tenant
    public List<Payment> getPaymentsByTenant(Long tenantId) {
        return paymentRepository.findByTenantIdOrderByPaymentDateDesc(tenantId);
    }

    // Get a single payment
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    // Create a new payment
    public Payment createPayment(Long tenantId, Payment payment) {
        Optional<Tenant> tenant = tenantRepository.findById(tenantId);
        if (tenant.isEmpty()) {
            throw new IllegalArgumentException("Tenant not found with id: " + tenantId);
        }
        payment.setTenant(tenant.get());
        return paymentRepository.save(payment);
    }

    // Update a payment
    public Optional<Payment> updatePayment(Long id, Payment paymentDetails) {
        return paymentRepository.findById(id)
                .map(payment -> {
                    if (paymentDetails.getAmount() != null) {
                        payment.setAmount(paymentDetails.getAmount());
                    }
                    if (paymentDetails.getPaymentDate() != null) {
                        payment.setPaymentDate(paymentDetails.getPaymentDate());
                    }
                    if (paymentDetails.getPaymentMethod() != null) {
                        payment.setPaymentMethod(paymentDetails.getPaymentMethod());
                    }
                    if (paymentDetails.getDescription() != null) {
                        payment.setDescription(paymentDetails.getDescription());
                    }
                    return paymentRepository.save(payment);
                });
    }

    // Delete a payment
    public boolean deletePayment(Long id) {
        return paymentRepository.findById(id)
                .map(payment -> {
                    paymentRepository.delete(payment);
                    return true;
                })
                .orElse(false);
    }

    // Get total paid by tenant
    public Double getTotalPaidByTenant(Long tenantId) {
        return getPaymentsByTenant(tenantId)
                .stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }
}