package com.Rentals.app.model;
import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {
    //Variables that are used to create the Room table in the database
    @Id
    @Column(nullable = false, unique = true)
    private Long RoomId;

    @Column(nullable = false)
    private String RoomType;

    @Column(nullable = true)
    private String RoomDescription;

    @Column(nullable = false)
    private Double RoomPrice;

    //Getters are used to get the values of the variables
    public Long getRoomId() {
        return RoomId;
    }

    public String getRoomType() {
        return RoomType;
    }

    public String getRoomDescription() {
        return RoomDescription;
    }

    public Double getRoomPrice() {
        return RoomPrice;
    }

    //Setters are used to set the values of the variables
    public void setRoomId(Long RoomId) {
        this.RoomId = RoomId;
    }

    public void setRoomType(String RoomType) {
        this.RoomType = RoomType;
    }

    public void setRoomDescription(String RoomDescription) {
        this.RoomDescription = RoomDescription;
    }

    public void setRoomPrice(Double RoomPrice) {
        this.RoomPrice = RoomPrice;
    }
}
