package com.team09.models;

import java.time.LocalDateTime;
// Xóa: import java.util.List;

/**
 * Lớp Chuyến bay nội địa.
 */
public class DomesticFlight extends Flight {
    private static final long serialVersionUID = 1L;

    // SỬA CONSTRUCTOR: Xóa "List<Seat> seats"
    public DomesticFlight(String flightId, String origin, String destination,
                          LocalDateTime departureTime, LocalDateTime arrivalTime,
                          double basePrice, String planeId) {
        // SỬA SUPER: Xóa "seats"
        super(flightId, origin, destination, departureTime, arrivalTime, basePrice, planeId);
    }

    @Override
    public String getFlightType() {
        return "Domestic";
    }
}