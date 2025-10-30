package com.team09.models;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Lớp Vé (lưu thông tin vé đã bán).
 */
public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ticketId;
    private String flightId;
    private String customerId;
    private String seatNumber; // Số ghế đã đặt
    private double finalPrice; // Giá cuối cùng (đã bao gồm phụ phí)
    private LocalDateTime bookingTime; // Giờ đặt vé

    public Ticket(String ticketId, String flightId, String customerId, String seatNumber, double finalPrice, LocalDateTime bookingTime) {
        this.ticketId = ticketId;
        this.flightId = flightId;
        this.customerId = customerId;
        this.seatNumber = seatNumber;
        this.finalPrice = finalPrice;
        this.bookingTime = bookingTime;
    }

    // Getters
    public String getTicketId() { return ticketId; }
    public String getFlightId() { return flightId; }
    public String getCustomerId() { return customerId; }
    public String getSeatNumber() { return seatNumber; }
    public double getFinalPrice() { return finalPrice; }
    public LocalDateTime getBookingTime() { return bookingTime; }
}