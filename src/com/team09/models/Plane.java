package com.team09.models;

import java.io.Serializable;

/**
 * Lớp Máy bay. Dùng làm "khuôn mẫu" để tạo ghế cho chuyến bay.
 */
public class Plane implements Serializable {
    private static final long serialVersionUID = 1L;

    private String planeId;
    private int economySeats; // Số lượng ghế phổ thông
    private int businessSeats; // Số lượng ghế thương gia
    private int firstClassSeats; // Số lượng ghế hạng nhất

    public Plane(String planeId, int economySeats, int businessSeats, int firstClassSeats) {
        this.planeId = planeId;
        this.economySeats = economySeats;
        this.businessSeats = businessSeats;
        this.firstClassSeats = firstClassSeats;
    }

    // Getters
    public String getPlaneId() { return planeId; }
    public int getEconomySeats() { return economySeats; }
    public int getBusinessSeats() { return businessSeats; }
    public int getFirstClassSeats() { return firstClassSeats; }

    public int getTotalSeats() {
        return economySeats + businessSeats + firstClassSeats;
    }
}