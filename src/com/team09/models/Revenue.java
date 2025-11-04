package com.team09.models;

import java.io.Serializable;

/**
 * Lớp thống kê Doanh thu (hàng ngày hoặc hàng tháng).
 */
public class Revenue implements Serializable {
    private static final long serialVersionUID = 1L;

    private String date; // Ngày (YYYY-MM-DD) hoặc Tháng (YYYY-MM)
    private double totalRevenue;
    private int ticketCount;
    private String type; // DAILY hoặc MONTHLY

    // Constructor dựa trên file revenue.csv bạn cung cấp (đã thay đổi để phù hợp hơn)
    public Revenue(String date, double totalRevenue, int ticketCount, String type) {
        this.date = date;
        this.totalRevenue = totalRevenue;
        this.ticketCount = ticketCount;
        this.type = type;
    }

    // Getters
    public String getDate() { return date; }
    public double getTotalRevenue() { return totalRevenue; }
    public int getTicketCount() { return ticketCount; }
    public String getType() { return type; }

    // Setters (Nếu cần)
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public void setTicketCount(int ticketCount) { this.ticketCount = ticketCount; }

    @Override
    public String toString() {
        return "Revenue [Date=" + date + ", Total=" + totalRevenue + ", Count=" + ticketCount + ", Type=" + type + "]";
    }
}