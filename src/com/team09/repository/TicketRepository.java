package com.team09.repository;

import com.team09.models.Ticket;
import java.time.*;
import java.util.*;

public class TicketRepository extends BaseRepository<Ticket> {
    public TicketRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected Ticket parse(String[] f) {
        try {
            return new Ticket(
                    f[0], // ticketId
                    f[1], // flightId
                    f[2], // customerId
                    f[3], // seatNumber
                    Double.parseDouble(f[4]), // finalPrice
                    LocalDateTime.parse(f[5]) // bookingTime
            );
        } catch (Exception e) {
            System.err.println("Lỗi parse Ticket: " + Arrays.toString(f));
            return null;
        }
    }

    @Override
    protected String toCsv(Ticket t) {
        return String.join(",",
                t.getTicketId(),
                t.getFlightId(),
                t.getCustomerId(),
                t.getSeatNumber(),
                String.valueOf(t.getFinalPrice()),
                t.getBookingTime().toString());
    }

    @Override
    protected String getHeader() {
        return "ticketId,flightId,customerId,seatNumber,finalPrice,bookingTime";
    }
}
