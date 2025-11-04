package com.team09.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Lớp trừu tượng cho Chuyến bay.
 */
public abstract class Flight implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String flightId;
    protected String origin;
    protected String destination;
    protected LocalDateTime departureTime;
    protected LocalDateTime arrivalTime;
    protected double basePrice;
    protected String planeId;

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

    // --- Phương thức trừu tượng ---
    public abstract String getFlightType();

    // --- Getters và Setters ---
    public String getFlightId() { return flightId; }
    public void setFlightId(String flightId) { this.flightId = flightId; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public double getBasePrice() { return basePrice; }
    public String getPlaneId() { return planeId; }
    public void setPlaneId(String planeId) { this.planeId = planeId; }

    /**
     * Lấy route (tuyến bay) dưới dạng chuỗi (ví dụ: HAN-SGN).
     */
    public String getRoute() {
        return this.origin + "-" + this.destination;
    }

    @Override
    public String toString() {
        return "Flight [ID=" + flightId + ", Route=" + origin + "->" + destination + ", Dep=" + departureTime + "]";
    }
}