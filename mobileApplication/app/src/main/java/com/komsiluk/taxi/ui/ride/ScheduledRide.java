package com.komsiluk.taxi.ui.ride;

import java.io.Serializable;
import java.util.ArrayList;

public class ScheduledRide implements Serializable {


    private final Long id;
    private final String name;
    private final String pickup;
    private final String destination;
    private final ArrayList<String> stations;
    private final ArrayList<String> users;
    private final String carType;
    private final boolean petFriendly;
    private final boolean childSeat;
    private final String km;
    private final String time;
    private final String scheduledTime;


    public ScheduledRide(
            Long id,
            String name,
            String pickup,
            String destination,
            ArrayList<String> stations,
            ArrayList<String> users,
            String carType,
            boolean petFriendly,
            boolean childSeat,
            String km,
            String time,
            String scheduledTime
    ) {
        this.id = id;
        this.name = name;
        this.pickup = pickup;
        this.destination = destination;
        this.stations = stations;
        this.users = users;
        this.carType = carType;
        this.petFriendly = petFriendly;
        this.childSeat = childSeat;
        this.km = km;
        this.time = time;
        this.scheduledTime = scheduledTime;
    }
    public Long getId() {return id;}

    public String getName() { return name; }
    public String getPickup() { return pickup; }
    public String getDestination() { return destination; }
    public ArrayList<String> getStations() { return stations; }
    public ArrayList<String> getUsers() { return users; }
    public String getCarType() { return carType; }
    public boolean isPetFriendly() { return petFriendly; }
    public boolean isChildSeat() { return childSeat; }
    public String getKm() { return km; }
    public String getTime() { return time; }
    public String getScheduledTime() { return scheduledTime; }
}
