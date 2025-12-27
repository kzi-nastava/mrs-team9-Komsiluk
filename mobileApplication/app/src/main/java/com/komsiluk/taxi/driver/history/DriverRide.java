package com.komsiluk.taxi.driver.history;

public class DriverRide {
    public final String date;        // npr "13.12.2025"
    public final String startTime;   // "12:00"
    public final String endTime;     // "14:14"
    public final String pickup;
    public final String destination;

    public final String status;      // "completed"
    public final int passengers;     // 3

    public final int kilometers;     // 100
    public final String duration;    // "2h 14min"
    public final String price;       // "200$"

    public DriverRide(String date, String startTime, String endTime,
                      String pickup, String destination,
                      String status, int passengers,
                      int kilometers, String duration, String price) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pickup = pickup;
        this.destination = destination;
        this.status = status;
        this.passengers = passengers;
        this.kilometers = kilometers;
        this.duration = duration;
        this.price = price;
    }
}
