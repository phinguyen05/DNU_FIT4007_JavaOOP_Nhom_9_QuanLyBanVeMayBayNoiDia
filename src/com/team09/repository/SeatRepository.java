package com.team09.repository;

import com.team09.models.*;
import java.util.*;

/**
 * Repository quản lý danh sách ghế (Seat) — đọc/ghi từ file CSV.
 */
public class SeatRepository extends BaseRepository<Seat> {

    public SeatRepository(String filePath) {
        super(filePath);
    }

    // Hàm giúp parse an toàn SeatType (chấp nhận FIRST, FIRST_CLASS, FIST, First, v.v.)
    private SeatType parseSeatTypeLenient(String raw) {
        if (raw == null) return null;
        String s = raw.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        // sửa 1 số lỗi đánh máy thường gặp
        if (s.equals("FIST")) s = "FIRST";
        if (s.equals("FIRSTCLASS")) s = "FIRST";
        if (s.equals("FIRST_CLASS")) s = "FIRST";

        try {
            return SeatType.valueOf(s);
        } catch (IllegalArgumentException e) {
            // thử map thêm những từ thân thiện
            if (s.startsWith("BUS")) return SeatType.BUSINESS;
            if (s.startsWith("ECO")) return SeatType.ECONOMY;
            if (s.startsWith("FIR") || s.startsWith("1ST") || s.startsWith("FIRST")) return SeatType.FIRST_CLASS;
            return null;
        }
    }

    private SeatStatus parseSeatStatusLenient(String raw) {
        if (raw == null) return null;
        String s = raw.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        try {
            return SeatStatus.valueOf(s);
        } catch (IllegalArgumentException e) {
            if (s.startsWith("AV")) return SeatStatus.AVAILABLE;
            if (s.startsWith("BO") || s.startsWith("BK")) return SeatStatus.BOOKED;
            return null;
        }
    }

    @Override
    protected Seat parse(String[] f) {
        try {
            if (f == null || f.length < 4) {
                System.err.println("[SeatRepository] Dữ liệu ghế không hợp lệ (thiếu cột): " + Arrays.toString(f));
                return null;
            }

            String seatNumber = f[0] == null ? "" : f[0].trim();
            String flightId = f[1] == null ? "" : f[1].trim();
            String rawType = f[2] == null ? "" : f[2].trim();
            String rawStatus = f[3] == null ? "" : f[3].trim();

            SeatType type = parseSeatTypeLenient(rawType);
            if (type == null) {
                System.err.println("[SeatRepository] Không parse được SeatType từ: '" + rawType + "' (dòng: " + Arrays.toString(f) + ")");
                return null;
            }

            SeatStatus status = parseSeatStatusLenient(rawStatus);
            if (status == null) {
                System.err.println("[SeatRepository] Không parse được SeatStatus từ: '" + rawStatus + "' (dòng: " + Arrays.toString(f) + ")");
                return null;
            }

            Seat seat;
            switch (type) {
                case BUSINESS:
                    seat = new BusinessSeat(flightId, seatNumber);
                    break;
                case FIRST_CLASS:
                    seat = new FirstClassSeat(flightId, seatNumber);
                    break;
                case ECONOMY:
                default:
                    seat = new EconomySeat(flightId, seatNumber);
                    break;
            }

            seat.setStatus(status);
            return seat;

        } catch (Exception e) {
            System.err.println("[SeatRepository] Lỗi khi parse Seat: " + Arrays.toString(f) + " — " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected String toCsv(Seat s) {
        return String.join(",",
                s.getSeatNumber(),
                s.getFlightId(),
                s.getSeatType().name(),
                s.getStatus().name()
        );
    }

    @Override
    protected String getHeader() {
        return "seatNumber,flightId,seatType,status";
    }
}
