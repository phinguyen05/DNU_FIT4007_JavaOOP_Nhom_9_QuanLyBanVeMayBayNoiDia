package com.team09.main.services;

import com.team09.main.models.*;
import com.team09.main.repository.*;
import com.team09.models.*;
import com.team09.repository.FlightRepository;
import com.team09.repository.InvoiceRepository;
import com.team09.repository.TicketRepository;

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

    /**
     * Yêu cầu: Doanh thu theo tháng
     */
    public double getRevenueByMonth(int year, Month month) {
        return invoiceRepo.getAll().stream()
                .filter(invoice -> invoice.getPaymentTime().getYear() == year &&
                        invoice.getPaymentTime().getMonth() == month)
                .mapToDouble(Invoice::getTotalAmount)
                .sum();
    }

    /**
     * Yêu cầu: Tỷ lệ ghế trống/đã đặt theo từng chuyến
     */
    public Map<String, Double> getOccupancyRateByFlight(String flightId) {
        List<Seat> seats = seatRepo.findByFlightId(flightId); // Cần thêm hàm này
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

    /**
     * Yêu cầu: Top 3 đường bay doanh thu cao nhất
     * Đây là logic phức tạp nhất
     */
    public List<Map.Entry<String, Double>> getTop3RoutesByRevenue() {
        // Lấy tất cả các vé đã bán
        List<Ticket> allTickets = ticketRepo.getAll();

        // 1. Nhóm các vé theo FlightId và tính tổng doanh thu cho mỗi Flight
        Map<String, Double> revenuePerFlight = allTickets.stream()
                .collect(Collectors.groupingBy(
                        Ticket::getFlightId,
                        Collectors.summingDouble(Ticket::getTotalPrice)
                ));

        // 2. Chuyển từ FlightId sang Route (SGN-HAN)
        // Cần một Map để cộng dồn doanh thu nếu nhiều chuyến bay có cùng 1 route
        Map<String, Double> revenuePerRoute = revenuePerFlight.entrySet().stream()
                .map(entry -> {
                    Flight f = flightRepo.findById(entry.getKey());
                    // Trả về một cặp (Key: Route, Value: Revenue)
                    return Map.entry(f != null ? f.getRoute() : "UNKNOWN", entry.getValue());
                })
                .filter(entry -> !entry.getKey().equals("UNKNOWN"))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.summingDouble(Map.Entry::getValue)
                ));

        // 3. Sắp xếp và lấy top 3
        return revenuePerRoute.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed()) // Sắp xếp giảm dần
                .limit(3) // Lấy 3 phần tử đầu
                .collect(Collectors.toList());
    }
}