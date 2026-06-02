package com.Rentals.app.controller;
import com.Rentals.app.model.Rentcharge;
import com.Rentals.app.service.RentchargeService;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rentcharges")
public class RentchargesController {
    private final RentchargeService rentchargeService;

    public RentchargesController(RentchargeService rentchargeService) {
        this.rentchargeService = rentchargeService;
    }

    @GetMapping
    public List<Rentcharge> getAllRentcharges() {
        return rentchargeService.getAllRentcharges();
    }

    @GetMapping("/search")
    public List<Rentcharge> searchRentcharges(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) String dueDate) {
        return rentchargeService.searchRentcharges(tenantId, dueDate);
    }

    @GetMapping("/tenant/{tenantId}")
    public List<Rentcharge> getRentchargesByTenantId(@PathVariable Long tenantId) {
        return rentchargeService.searchRentcharges(tenantId, null);
    }

    @GetMapping("/date/{dueDate}")
    public List<Rentcharge> getRentchargesByDueDate(@PathVariable String dueDate) {
        return rentchargeService.searchRentcharges(null, dueDate);
    }

    @PostMapping
    public ResponseEntity<Rentcharge> createRentcharge(@RequestBody Rentcharge rentcharge) {
        try {
            Rentcharge savedRentcharge = rentchargeService.createRentcharge(rentcharge);
            return ResponseEntity.ok(savedRentcharge);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("required")) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
    
}
