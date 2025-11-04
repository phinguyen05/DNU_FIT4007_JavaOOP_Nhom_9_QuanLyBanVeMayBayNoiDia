package com.team09.util;

import com.team09.models.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp tiện ích chịu trách nhiệm sinh ra các đối tượng ghế (Seat)
 * dựa trên cấu hình của Máy bay (Plane) và gán cho Chuyến bay (Flight).
 * Đảm bảo nguyên tắc Single Responsibility Principle.
 */
public class SeatGenerator {

    /**
     * Sinh danh sách ghế cho một chuyến bay dựa trên cấu hình máy bay.
     * @param flightId Mã chuyến bay cần sinh ghế.
     * @param plane Đối tượng máy bay chứa cấu hình số lượng ghế.
     * @return Danh sách các đối tượng Seat đã được khởi tạo.
     */
    public static List<Seat> generateSeats(String flightId, Plane plane) {
        List<Seat> seats = new ArrayList<>();

        // 1. Sinh Ghế Hạng Phổ thông (Economy)
        // Ký hiệu: E01, E02, ...
        for (int i = 1; i <= plane.getEconomySeats(); i++) {
            String seatNumber = "E" + String.format("%02d", i);
            seats.add(new EconomySeat(flightId, seatNumber));
        }

        // 2. Sinh Ghế Hạng Thương gia (Business)
        // Ký hiệu: B01, B02, ...
        for (int i = 1; i <= plane.getBusinessSeats(); i++) {
            String seatNumber = "B" + String.format("%02d", i);
            seats.add(new BusinessSeat(flightId, seatNumber));
        }

        // 3. Sinh Ghế Hạng Nhất (First Class)
        // Ký hiệu: F01, F02, ...
        for (int i = 1; i <= plane.getFirstClassSeats(); i++) {
            String seatNumber = "F" + String.format("%02d", i);
            seats.add(new FirstClassSeat(flightId, seatNumber));
        }

        return seats;
    }
}