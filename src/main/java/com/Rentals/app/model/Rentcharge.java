package com.Rentals.app.model;
import jakarta.persistence.*;

@Entity
@Table(name = "rentcharges")
public class Rentcharge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "charge_amount", nullable = false)
    private Double chargeAmount;

    @ManyToOne(optional = false)
    @JoinColumn(
        name = "tenant_id",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(name = "fk_rentcharge_tenant")
    )
    private Tenant tenant;

    @Column(name = "due_date", nullable = false)
    private String dueDate;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(Double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
