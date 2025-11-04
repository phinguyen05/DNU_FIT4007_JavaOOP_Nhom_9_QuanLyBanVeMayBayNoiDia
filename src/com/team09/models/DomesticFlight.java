package com.team09.models;

import java.time.LocalDateTime;

/**
 * Lớp Chuyến bay nội địa.
 */
public class DomesticFlight extends Flight {
    private static final long serialVersionUID = 1L;

    // SỬA CONSTRUCTOR: Đảm bảo khớp với constructor của Flight
    public DomesticFlight(String flightId, String origin, String destination,
                          LocalDateTime departureTime, LocalDateTime arrivalTime,
                          double basePrice, String planeId) {
        super(flightId, origin, destination, departureTime, arrivalTime, basePrice, planeId);
    }

    @Override
    public String getFlightType() {
        return "Domestic";
    }
}