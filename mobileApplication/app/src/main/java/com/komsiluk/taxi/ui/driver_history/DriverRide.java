package com.komsiluk.taxi.ui.driver_history;

import java.util.List;

public class DriverRide {
    public final Long id;
    public final String date;        // npr "13.12.2025"
    public final String startTime;   // "12:00"
    public final String endTime;     // "14:14"
    public final String pickup;
    public List<String> stops;

    public final String destination;

    public final String status;      // "completed"
    public final int passengers;     // 3

    public final double kilometers;     // 100
    public final String duration;    // "2h 14min"
    public final String price;       // "200$"

    public List<String> passengerEmails; // Dodaj ovo
    public boolean isPanicPressed;      // Dodaj ovo

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
