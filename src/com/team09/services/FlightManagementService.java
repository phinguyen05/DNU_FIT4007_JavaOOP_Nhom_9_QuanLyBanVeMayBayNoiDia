package com.team09.main.services;

import com.team09.main.models.*;
import com.team09.main.repository.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlightManagementService {

    private final FlightRepository flightRepo;
    private final PlaneRepository planeRepo;
    private final SeatRepository seatRepo; // Cần để tự động tạo ghế khi tạo chuyến bay

    public FlightManagementService(FlightRepository flightRepo, PlaneRepository planeRepo, SeatRepository seatRepo) {
        this.flightRepo = flightRepo;
        this.planeRepo = planeRepo;
        this.seatRepo = seatRepo;
    }

    /**
     * Tạo chuyến bay mới, bao gồm cả việc kiểm tra trùng lịch và tạo ghế tự động.
     * @return Chuyến bay đã tạo
     * @throws Exception nếu lịch bị trùng hoặc máy bay không tồn tại
     */
    public Flight createFlight(String planeId, String departurePoint, String destinationPoint,
                               LocalDateTime departureTime, LocalDateTime arrivalTime, double basePrice) throws Exception {

        Plane plane = planeRepo.findById(planeId);
        if (plane == null) {
            throw new Exception("Không tìm thấy máy bay với ID: " + planeId);
        }

        // 1. Logic nghiệp vụ cốt lõi: Kiểm tra trùng lịch
        if (isPlaneScheduleOverlap(planeId, departureTime, arrivalTime)) {
            throw new Exception("Lịch bay bị trùng! Máy bay " + planeId + " đã được xếp lịch vào thời gian này.");
        }

        // 2. Tạo chuyến bay
        String flightId = "F-" + UUID.randomUUID().toString().substring(0, 8);
        DomesticFlight newFlight = new DomesticFlight(/*...thêm constructor cho D.Flight...*/);
        newFlight.setFlightId(flightId);
        newFlight.setPlaneId(planeId);
        //... set các thuộc tính khác ...

        flightRepo.add(newFlight);

        // 3. Tự động tạo ghế cho chuyến bay dựa trên máy bay
        generateSeatsForFlight(newFlight, plane);

        return newFlight;
    }

    /**
     * Tự động tạo ghế (Economy, Business, First Class) cho một chuyến bay mới.
     */
    private void generateSeatsForFlight(Flight flight, Plane plane) {
        List<Seat> newSeats = new ArrayList<>();
        int seatCount = 0;

        // Giả sử Plane có getEconomySeats(), getBusinessSeats()...
        for (int i = 0; i < plane.getEconomySeatCount(); i++) {
            String seatId = "S-" + UUID.randomUUID().toString().substring(0, 8);
            String seatNum = "E" + (i + 1);
            newSeats.add(new EconomySeat(seatId, flight.getFlightId(), seatNum));
        }
        for (int i = 0; i < plane.getBusinessSeatCount(); i++) {
            String seatId = "S-" + UUID.randomUUID().toString().substring(0, 8);
            String seatNum = "B" + (i + 1);
            newSeats.add(new BusinessSeat(seatId, flight.getFlightId(), seatNum));
        }
        // Tương tự cho FirstClassSeat...

        // Lưu toàn bộ ghế mới vào repository
        seatRepo.addAll(newSeats); // Giả sử có hàm addAll
    }

    /**
     * LOGIC KIỂM TRA TRÙNG LỊCH:
     * Kiểm tra xem một máy bay có bị xếp 2 chuyến bay trùng thời gian không.
     * Overlap logic: (StartA < EndB) và (EndA > StartB)
     */
    private boolean isPlaneScheduleOverlap(String planeId, LocalDateTime newStart, LocalDateTime newEnd) {
        List<Flight> flightsForThisPlane = flightRepo.findByPlaneId(planeId); // Cần thêm hàm này trong Repo

        for (Flight existingFlight : flightsForThisPlane) {
            LocalDateTime existingStart = existingFlight.getDepartureTime();
            LocalDateTime existingEnd = existingFlight.getArrivalTime();

            // Check overlap
            if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                return true; // Bị trùng
            }
        }
        return false; // Không trùng
    }

    // ... các hàm quản lý máy bay (addPlane, removePlane...)
}