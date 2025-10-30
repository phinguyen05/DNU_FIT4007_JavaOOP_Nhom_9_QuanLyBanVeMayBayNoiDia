package com.team09.models;

import java.io.Serializable;
import java.time.LocalDateTime;
// Xóa: import java.util.List;

public abstract class Flight implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String flightId;
    protected String origin;
    protected String destination;
    protected LocalDateTime departureTime;
    protected LocalDateTime arrivalTime;
    protected double basePrice;
    protected String planeId;

    // XÓA: protected List<Seat> seats;

    // SỬA CONSTRUCTOR: Xóa "List<Seat> seats"
    public Flight(String flightId, String origin, String destination,
                  LocalDateTime departureTime, LocalDateTime arrivalTime,
                  double basePrice, String planeId) {
        this.flightId = flightId;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.basePrice = basePrice;
        this.planeId = planeId;
    }

    public abstract String getFlightType();

    // Getters (XÓA getSeats() và setSeats())
    public String getFlightId() { return flightId; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public double getBasePrice() { return basePrice; }
    public String getPlaneId() { return planeId; }

    public String getRoute() {
        return origin + " -> ";
    }
}