package com.team09.models;

import java.io.Serializable;

/**
 * Lớp trừu tượng cho Ghế.
 * Quản lý trạng thái và tính phụ phí (đa hình).
 */
public abstract class Seat implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String seatNumber; // Ví dụ: "A1", "B12"
    protected SeatStatus status;
    protected String flightId; // Ghế này thuộc chuyến bay nào

    public Seat(String flightId, String seatNumber) {
        this.flightId = flightId;
        this.seatNumber = seatNumber;
        this.status = SeatStatus.AVAILABLE; // Mặc định là còn trống
    }

    // --- Phương thức đa hình ---

    /**
     * Lấy phụ phí cho loại ghế này.
     * @return số tiền phụ phí
     */
    public abstract double getSurcharge();

    /**
     * Lấy loại ghế (Economy, Business, First Class).
     * @return Enum SeatType
     */
    public abstract SeatType getSeatType();

    // --- Getters and Setters ---
    public String getSeatNumber() { return seatNumber; }
    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }
    public String getFlightId() { return flightId; }

    @Override
    public String toString() {
        return "Seat [" + seatNumber + ", Type=" + getSeatType() + ", Status=" + status + "]";
    }
}