package com.team09.repository;

import com.team09.models.DomesticFlight;
import java.time.*;
import java.util.*;

public class FlightRepository extends BaseRepository<DomesticFlight> {

    public FlightRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected DomesticFlight parse(String[] f) {
        try {
            // f[0]=flightId, f[1]=planeId, f[2]=from, f[3]=to, f[4]=departDate, f[5]=departTime, f[6]=arrivalTime, f[7]=basePrice
            LocalDate departDate = LocalDate.parse(f[4]);
            LocalTime departTime = LocalTime.parse(f[5]);
            LocalTime arrivalTime = LocalTime.parse(f[6]);

            LocalDateTime departDateTime = LocalDateTime.of(departDate, departTime);
            LocalDateTime arrivalDateTime = LocalDateTime.of(departDate, arrivalTime);

            return new DomesticFlight(
                    f[0],        // flightId
                    f[2],        // origin (from)
                    f[3],        // destination (to)
                    departDateTime,
                    arrivalDateTime,
                    Double.parseDouble(f[7]),
                    f[1]         // planeId
            );

        } catch (Exception e) {
            System.err.println("Lỗi parse Flight: " + Arrays.toString(f));
            return null;
        }
    }

    @Override
    protected String toCsv(DomesticFlight fl) {
        return String.join(",",
                fl.getFlightId(),
                fl.getPlaneId(),
                fl.getOrigin(),
                fl.getDestination(),
                fl.getDepartureTime().toLocalDate().toString(),
                fl.getDepartureTime().toLocalTime().toString(),
                fl.getArrivalTime().toLocalTime().toString(),
                String.valueOf(fl.getBasePrice())
        );
    }

    @Override
    protected String getHeader() {
        return "flightId,planeId,from,to,departDate,departTime,arrivalTime,basePrice";
    }
}
