package com.Rentals.app.model;
import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {
    //Variables that are used to create the Room table in the database
    @Id
    @Column(name = "room_id", nullable = false, unique = true)
    private Long roomId;

    @Column(name = "room_type", nullable = false)
    private String roomType;

    @Column(name = "room_description", nullable = true)
    private String roomDescription;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "room_price", nullable = false)
    private Double roomPrice;

    //Getters are used to get the values of the variables
    public Long getRoomId() {
        return roomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public String getStatus() {
        return status;
    }

    public Double getRoomPrice() {
        return roomPrice;
    }

    //Setters are used to set the values of the variables
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRoomPrice(Double roomPrice) {
        this.roomPrice = roomPrice;
    }

  
}
