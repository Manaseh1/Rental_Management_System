package com.Rentals.app.controller;

import com.Rentals.app.model.Payment;
import com.Rentals.app.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<Payment> getAllPayments() {
         return paymentService.getAllPayments();
}
    // Get all payments for a tenant
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Payment>> getPaymentsByTenant(@PathVariable Long tenantId) {
        List<Payment> payments = paymentService.getPaymentsByTenant(tenantId);
        return ResponseEntity.ok(payments);
    }

    // Get a single payment
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    // @GetMapping("/payment-history")
    // public String showPaymentHistory() {
    //     return "payment-history.html";
// }
    // Create a new payment
    @PostMapping("/tenant/{tenantId}")
    public ResponseEntity<Payment> createPayment(@PathVariable Long tenantId, @RequestBody Payment payment) {
        try {
            Payment savedPayment = paymentService.createPayment(tenantId, payment);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPayment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Update a payment
    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Payment paymentDetails) {
        return paymentService.updatePayment(id, paymentDetails)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a payment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        if (paymentService.deletePayment(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Get total paid by tenant
    @GetMapping("/tenant/{tenantId}/total")
    public ResponseEntity<Double> getTotalPaidByTenant(@PathVariable Long tenantId) {
        Double total = paymentService.getTotalPaidByTenant(tenantId);
        return ResponseEntity.ok(total);
    }
}
