package com.team09.models;

public class FirstClassSeat extends Seat {
    private static final long serialVersionUID = 1L;

    // Phụ phí cố định cho hạng nhất (ví dụ)
    private static final double SURCHARGE_AMOUNT = 2500000.0;

    public FirstClassSeat(String flightId, String seatNumber) {
        super(flightId, seatNumber);
    }

    @Override
    public double getSurcharge() {
        return SURCHARGE_AMOUNT;
    }

    @Override
    public SeatType getSeatType() {
        return SeatType.FIRST_CLASS;
    }
}