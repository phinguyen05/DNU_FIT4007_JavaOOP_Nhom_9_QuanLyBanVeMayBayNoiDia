package com.team09.services;

import com.team09.models.Flight;
import com.team09.models.Ticket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class StandardRefundPolicy implements RefundPolicy {

    @Override
    public double calculateRefund(Ticket ticket, Flight flight) {
        LocalDateTime flightDateTime = flight.getDepartureTime();
        LocalDateTime cancellationTime = LocalDateTime.now();
        double originalPrice = ticket.getFinalPrice();

        long hoursBeforeFlight = ChronoUnit.HOURS.between(cancellationTime, flightDateTime);

        if (hoursBeforeFlight < 0) {
            return 0.0;
        } else if (hoursBeforeFlight < 48) {
            return originalPrice * 0.50;
        } else {
            return originalPrice * 0.90;
        }
    }
}