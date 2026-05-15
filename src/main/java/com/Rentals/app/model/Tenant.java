package com.Rentals.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tenants")
public class Tenant {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email", nullable = true, unique = true)
    private String email;

    @Column(name = "balance", nullable = true)
    private Double balance;

    @ManyToOne(optional = false)
    @JoinColumn(
        name = "room_id",
        referencedColumnName = "room_id",
        foreignKey = @ForeignKey(name = "fk_tenant_room")
    )
    private Room room;

    // getters/setters...
}