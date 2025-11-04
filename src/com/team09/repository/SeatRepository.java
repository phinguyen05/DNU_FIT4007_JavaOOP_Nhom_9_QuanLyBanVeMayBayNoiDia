package com.team09.repository;

import  com.team09.models.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Predicate;

public class SeatRepository extends BaseRepository<Seat> {

    // ... (các phương thức parse, toCsv, getHeader, getId giữ nguyên) ...
    // Giữ nguyên các phương thức tiện ích parseSeatTypeLenient và parseSeatStatusLenient

    public SeatRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected Seat parse(String[] f) {
        // Logic parse đã được cung cấp
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
                // Đã có in lỗi trong parseSeatTypeLenient
                return null;
            }

            SeatStatus status = parseSeatStatusLenient(rawStatus);
            if (status == null) {
                // Đã có in lỗi trong parseSeatStatusLenient
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
            return null;
        }
    }

    private SeatType parseSeatTypeLenient(String raw) {
        if (raw == null) return null;
        String s = raw.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        if (s.equals("FIST")) s = "FIRST_CLASS"; // Sửa lỗi này cho đúng enum
        if (s.equals("FIRST")) s = "FIRST_CLASS";
        if (s.equals("FIRSTCLASS")) s = "FIRST_CLASS";

        try {
            return SeatType.valueOf(s);
        } catch (IllegalArgumentException e) {
            if (s.startsWith("BUS")) return SeatType.BUSINESS;
            if (s.startsWith("ECO")) return SeatType.ECONOMY;
            if (s.startsWith("FIR") || s.startsWith("1ST")) return SeatType.FIRST_CLASS;
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

    @Override
    protected String getId(Seat s) {
        return s.getFlightId() + "-" + s.getSeatNumber();
    }

    public List<Seat> findByFlightId(String flightId) {
        return loadAll().stream()
                .filter(s -> s.getFlightId().equals(flightId))
                .collect(Collectors.toList());
    }

    @Override
    public Seat findById(String id) { // ID format: flightId-seatNumber
        return loadAll().stream()
                .filter(s -> (s.getFlightId() + "-" + s.getSeatNumber()).equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Xóa tất cả các ghế thuộc về một chuyến bay cụ thể.
     * @param flightId Mã chuyến bay.
     */
    public void deleteByFlightId(String flightId) {
        List<Seat> all = loadAll();
        Predicate<Seat> seatFilter = seat -> !seat.getFlightId().equals(flightId);
        List<Seat> updatedList = all.stream().filter(seatFilter).collect(Collectors.toList());

        if (updatedList.size() < all.size()) {
            saveAll(updatedList);
        } else {
            // Không tìm thấy ghế nào, không cần thông báo lỗi
        }
    }
}