package com.Rentals.app.model;
import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {
    //Variables that are used to create the Room table in the database
    @Id
    @Column(name = "room_id", nullable = false, unique = true)

    private Long room_id;

    @Column(name = "room_type", nullable = false)

    private String room_type;

    @Column(name = "room_description", nullable = true)

    private String room_description;

    @Column(name = "room_price", nullable = false)

    private Double room_price;

    //Getters are used to get the values of the variables
    public Long getRoomId() {
        return room_id;
    }

    public String getRoomType() {
        return room_type;
    }

    public String getRoomDescription() {
        return room_description;
    }
    

    public Double getRoomPrice() {
        return room_price;
    }

    //Setters are used to set the values of the variables
    public void setRoomId(Long room_id) {
        this.room_id = room_id;
    }

    public void setRoomType(String room_type) {
        this.room_type = room_type;
    }

    public void setRoomDescription(String room_description) {
        this.room_description = room_description;
    }

    public void setRoomPrice(Double room_price) {
        this.room_price = room_price;
    }
}
