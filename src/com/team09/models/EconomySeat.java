package com.team09.models;

public class EconomySeat extends Seat {
    private static final long serialVersionUID = 1L;

    public EconomySeat(String flightId, String seatNumber) {
        super(flightId, seatNumber);
    }

    @Override
    public double getSurcharge() {
        // Ghế phổ thông không có phụ phí
        return 0.0;
    }

    @Override
    public SeatType getSeatType() {
        return SeatType.ECONOMY;
    }
}