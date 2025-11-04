package com.team09.services;

import com.team09.models.*;
import com.team09.repository.*;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {

    private final InvoiceRepository invoiceRepo;
    private final TicketRepository ticketRepo;
    private final FlightRepository flightRepo;
    private final SeatRepository seatRepo;

    public ReportService(InvoiceRepository invoiceRepo, TicketRepository ticketRepo,
                         FlightRepository flightRepo, SeatRepository seatRepo) {
        this.invoiceRepo = invoiceRepo;
        this.ticketRepo = ticketRepo;
        this.flightRepo = flightRepo;
        this.seatRepo = seatRepo;
    }

    // Phương thức bổ sung để CLIMenu hiển thị trạng thái ghế
    public List<Seat> getAllSeatsByFlightId(String flightId) {
        return seatRepo.findByFlightId(flightId);
    }

    public double getRevenueByMonth(int year, Month month) {
        return invoiceRepo.getAll().stream()
                .filter(invoice -> invoice.getCreatedDate().getYear() == year &&
                        invoice.getCreatedDate().getMonth() == month)
                .mapToDouble(Invoice::getTotalAmount)
                .sum();
    }

    public Map<String, Double> getOccupancyRateByFlight(String flightId) {
        List<Seat> seats = seatRepo.findByFlightId(flightId);
        if (seats.isEmpty()) {
            return Map.of("BOOKED_RATE", 0.0, "AVAILABLE_RATE", 100.0);
        }

        long totalSeats = seats.size();
        long bookedSeats = seats.stream()
                .filter(s -> s.getStatus() == SeatStatus.BOOKED)
                .count();

        double bookedRate = (double) bookedSeats / totalSeats * 100.0;
        double availableRate = 100.0 - bookedRate;

        return Map.of("BOOKED_RATE", bookedRate, "AVAILABLE_RATE", availableRate);
    }

    public List<Map.Entry<String, Double>> getTop3RoutesByRevenue() {
        List<Ticket> allTickets = ticketRepo.getAll();

        Map<String, Double> revenuePerFlight = allTickets.stream()
                .collect(Collectors.groupingBy(
                        Ticket::getFlightId,
                        Collectors.summingDouble(Ticket::getFinalPrice)
                ));

        Map<String, Double> revenuePerRoute = revenuePerFlight.entrySet().stream()
                .map(entry -> {
                    Flight f = flightRepo.findById(entry.getKey());
                    return Map.entry(f != null ? f.getRoute() : "UNKNOWN", entry.getValue());
                })
                .filter(entry -> !entry.getKey().equals("UNKNOWN"))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.summingDouble(Map.Entry::getValue)
                ));

        return revenuePerRoute.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());
    }
}