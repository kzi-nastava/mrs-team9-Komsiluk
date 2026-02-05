package com.komsiluk.taxi.ui.ride;

import java.io.Serializable;
import java.util.ArrayList;

public class FavoriteRide implements Serializable {

    private final Long favoriteId;
    private final String name;
    private final String pickup;
    private final String destination;
    private final ArrayList<String> stations;
    private final ArrayList<String> users;

    private final String carType;
    private final boolean petFriendly;
    private final boolean childSeat;

    private final double distanceKm;
    private final int estimatedMin;
    private final Long routeId;

    public FavoriteRide(
            Long favoriteId,
            String name,
            String pickup,
            String destination,
            ArrayList<String> stations,
            ArrayList<String> users,
            String carType,
            boolean petFriendly,
            boolean childSeat,
            double distanceKm,
            int estimatedMin,
            Long routeId
    ) {
        this.favoriteId = favoriteId;
        this.name = name;
        this.pickup = pickup;
        this.destination = destination;
        this.stations = stations;
        this.users = users;
        this.carType = carType;
        this.petFriendly = petFriendly;
        this.childSeat = childSeat;
        this.distanceKm = distanceKm;
        this.estimatedMin = estimatedMin;
        this.routeId = routeId;
    }

    public Long getFavoriteId() { return favoriteId; }
    public String getName() { return name; }
    public String getPickup() { return pickup; }
    public String getDestination() { return destination; }
    public ArrayList<String> getStations() { return stations; }
    public ArrayList<String> getUsers() { return users; }
    public String getCarType() { return carType; }
    public boolean isPetFriendly() { return petFriendly; }
    public boolean isChildSeat() { return childSeat; }
    public double getDistanceKm() { return distanceKm; }
    public int getEstimatedMin() { return estimatedMin; }
    public Long getRouteId() { return routeId; }
}
