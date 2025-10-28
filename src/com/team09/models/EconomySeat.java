package com.team09.models;
public class EconomySeat extends Seat {
    public EconomySeat(String seatNumber, double basePrice) {
        super(seatNumber, basePrice);
    }
    @Override
    public double getSurcharge() {
        // Ghế Economy không có phụ phí tôi giả sử ở đây là 5.0
        return 5.0;
    }
}