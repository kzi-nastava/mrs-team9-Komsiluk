package com.komsiluk.taxi.ui.driver_history;

import java.util.List;

public class DriverRide {
    public final Long id;
    public final String date;
    public final String startTime;
    public final String endTime;
    public final String pickup;
    public List<String> stops;

    public final String destination;

    public final String status;
    public final int passengers;

    public final double kilometers;
    public final String duration;
    public final String price;

    public List<String> passengerEmails;
    public boolean isPanicPressed;

    public DriverRide(Long id, String date, String startTime, String endTime,
                      String pickup, List<String> stops, String destination,
                      String status, int passengers,
                      Double kilometers, String duration, String price) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pickup = pickup;
        this.stops = stops;
        this.destination = destination;
        this.status = status;
        this.passengers = passengers;
        this.kilometers = kilometers;
        this.duration = duration;
        this.price = price;
    }
}
