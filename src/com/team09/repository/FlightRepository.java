package com.team09.repository;

import com.team09.models.DomesticFlight;
import com.team09.models.Flight;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FlightRepository extends BaseRepository<Flight> {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public FlightRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected Flight parse(String[] f) {
        try {
            // Cấu trúc CSV: flightId,origin,destination,departureTime,arrivalTime,basePrice,planeId
            if (f.length < 7) {
                System.err.println("Dữ liệu Flight không hợp lệ (thiếu cột): " + Arrays.toString(f));
                return null;
            }

            LocalDateTime departDateTime = LocalDateTime.parse(f[3], DATETIME_FORMATTER);
            LocalDateTime arrivalDateTime = LocalDateTime.parse(f[4], DATETIME_FORMATTER);

            return new DomesticFlight(
                    f[0],        // flightId
                    f[1],        // origin
                    f[2],        // destination
                    departDateTime,
                    arrivalDateTime,
                    Double.parseDouble(f[5]), // basePrice
                    f[6]         // planeId
            );

        } catch (Exception e) {
            System.err.println("Lỗi parse Flight: " + Arrays.toString(f) + " -> " + e.getMessage());
            return null;
        }
    }

    @Override
    protected String toCsv(Flight fl) {
        return String.join(",",
                fl.getFlightId(),
                fl.getOrigin(),
                fl.getDestination(),
                fl.getDepartureTime().format(DATETIME_FORMATTER),
                fl.getArrivalTime().format(DATETIME_FORMATTER),
                String.valueOf(fl.getBasePrice()),
                fl.getPlaneId()
        );
    }

    @Override
    protected String getHeader() {
        return "flightId,origin,destination,departureTime,arrivalTime,basePrice,planeId";
    }

    @Override
    protected String getId(Flight f) {
        return f.getFlightId();
    }

    public List<Flight> findByPlaneId(String planeId) {
        return loadAll().stream()
                .filter(f -> f.getPlaneId().equals(planeId))
                .collect(Collectors.toList());
    }
}