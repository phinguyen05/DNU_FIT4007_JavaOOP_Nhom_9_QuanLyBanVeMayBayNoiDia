package com.team09.services;

import com.team09.exceptions.FlightNotFoundException;
import com.team09.exceptions.InvalidDeletionException;
import com.team09.exceptions.PlaneNotFoundException;
import com.team09.models.*;
import com.team09.repository.FlightRepository;
import com.team09.repository.PlaneRepository;
import com.team09.repository.SeatRepository;
import com.team09.util.SeatGenerator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class FlightManagementService {

    private final PlaneRepository planeRepository;
    private final FlightRepository flightRepository;
    private final SeatRepository seatRepository;

    public FlightManagementService(PlaneRepository planeRepository, FlightRepository flightRepository, SeatRepository seatRepository) {
        this.planeRepository = planeRepository;
        this.flightRepository = flightRepository;
        this.seatRepository = seatRepository;
    }

    // ===================================
    // 🛩️ QUẢN LÝ MÁY BAY (CRUD)
    // ===================================

    public Plane getPlaneById(String id) {
        return planeRepository.findById(id);
    }

    public List<Plane> getAllPlanes() {
        return planeRepository.getAll();
    }

    public void addPlane(Plane plane) throws Exception {
        if (planeRepository.findById(plane.getPlaneId()) != null) {
            throw new Exception("Mã máy bay " + plane.getPlaneId() + " đã tồn tại.");
        }
        planeRepository.add(plane);
        System.out.println("Máy bay " + plane.getPlaneId() + " đã được thêm thành công.");
    }

    public void updatePlane(Plane plane) throws PlaneNotFoundException {
        if (planeRepository.findById(plane.getPlaneId()) == null) {
            throw new PlaneNotFoundException("Không tìm thấy máy bay với mã: " + plane.getPlaneId());
        }
        planeRepository.update(plane);
        System.out.println("Máy bay " + plane.getPlaneId() + " đã được cập nhật thành công.");
    }

    public void deletePlane(String planeId) throws PlaneNotFoundException, InvalidDeletionException {
        Plane plane = planeRepository.findById(planeId);
        if (plane == null) {
            throw new PlaneNotFoundException("Không tìm thấy máy bay với mã: " + planeId);
        }

        List<Flight> dependentFlights = flightRepository.loadAll().stream()
                .filter(f -> f.getPlaneId().equals(planeId))
                .collect(Collectors.toList());

        if (!dependentFlights.isEmpty()) {
            throw new InvalidDeletionException("Không thể xóa máy bay " + planeId + " vì còn " + dependentFlights.size() + " chuyến bay đang sử dụng.");
        }

        planeRepository.delete(planeId);
        System.out.println("Máy bay " + planeId + " đã được xóa thành công.");
    }

    // ===================================
    // 🛫 QUẢN LÝ CHUYẾN BAY (CRUD)
    // ===================================

    public List<Flight> getAllFlights() {
        return flightRepository.getAll();
    }

    public Flight getFlightById(String flightId) {
        return flightRepository.findById(flightId);
    }

    public void createFlight(Flight flight) throws Exception {
        Plane plane = planeRepository.findById(flight.getPlaneId());
        if (plane == null) {
            throw new PlaneNotFoundException("Không tìm thấy máy bay với mã: " + flight.getPlaneId());
        }

        if (isPlaneScheduleOverlap(flight)) {
            throw new Exception("Lịch trình chuyến bay mới bị trùng với một chuyến bay khác của máy bay " + flight.getPlaneId() + ".");
        }

        if (flightRepository.findById(flight.getFlightId()) != null) {
            throw new Exception("Mã chuyến bay " + flight.getFlightId() + " đã tồn tại.");
        }

        flightRepository.add(flight);
        generateSeatsForFlight(flight, plane);
        System.out.println("Chuyến bay " + flight.getFlightId() + " đã được tạo thành công.");
    }

    public void updateFlight(Flight updatedFlight) throws FlightNotFoundException, Exception {
        Flight existingFlight = flightRepository.findById(updatedFlight.getFlightId());
        if (existingFlight == null) {
            throw new FlightNotFoundException("Không tìm thấy chuyến bay với mã: " + updatedFlight.getFlightId());
        }

        // Kiểm tra máy bay mới có tồn tại không
        if (!existingFlight.getPlaneId().equals(updatedFlight.getPlaneId())) {
            if (planeRepository.findById(updatedFlight.getPlaneId()) == null) {
                throw new PlaneNotFoundException("Không tìm thấy máy bay mới với mã: " + updatedFlight.getPlaneId());
            }
        }

        boolean hasBookedSeats = seatRepository.loadAll().stream()
                .anyMatch(s -> s.getFlightId().equals(updatedFlight.getFlightId()) && s.getStatus() == SeatStatus.BOOKED);

        if (hasBookedSeats) {
            // Nếu có vé đã đặt, chỉ cho phép thay đổi giá cơ bản, điểm đi/đến (ít ảnh hưởng)
            // Cấm thay đổi PlaneId, DepartureTime, ArrivalTime
            if (!existingFlight.getPlaneId().equals(updatedFlight.getPlaneId()) ||
                    !existingFlight.getDepartureTime().equals(updatedFlight.getDepartureTime()) ||
                    !existingFlight.getArrivalTime().equals(updatedFlight.getArrivalTime())) {

                throw new InvalidDeletionException("Không thể thay đổi lịch trình hoặc máy bay vì chuyến bay " + updatedFlight.getFlightId() + " đã có vé được đặt.");
            }
        }

        // Nếu có thay đổi máy bay VÀ không có vé nào được đặt (hoặc chỉ thay đổi PlaneId, logic ở trên đã cấm)
        // Ta cần xóa ghế cũ và tạo ghế mới (Trường hợp thay đổi PlaneId khi chưa có vé nào được đặt)
        if (!existingFlight.getPlaneId().equals(updatedFlight.getPlaneId()) && !hasBookedSeats) {
            Plane newPlane = planeRepository.findById(updatedFlight.getPlaneId());
            if (newPlane != null) {
                seatRepository.deleteByFlightId(updatedFlight.getFlightId()); // Xóa ghế cũ
                generateSeatsForFlight(updatedFlight, newPlane); // Tạo ghế mới
            }
        }

        // Kiểm tra trùng lịch
        if (isPlaneScheduleOverlap(updatedFlight)) {
            throw new Exception("Lịch trình chuyến bay cập nhật bị trùng với một chuyến bay khác của máy bay " + updatedFlight.getPlaneId() + ".");
        }

        flightRepository.update(updatedFlight);
        System.out.println("Chuyến bay " + updatedFlight.getFlightId() + " đã được cập nhật thành công.");
    }

    public void deleteFlight(String flightId) throws FlightNotFoundException, InvalidDeletionException {
        Flight flight = flightRepository.findById(flightId);
        if (flight == null) {
            throw new FlightNotFoundException("Không tìm thấy chuyến bay với mã: " + flightId);
        }

        if (seatRepository.loadAll().stream().anyMatch(s -> s.getFlightId().equals(flightId) && s.getStatus() == SeatStatus.BOOKED)) {
            throw new InvalidDeletionException("Không thể xóa chuyến bay " + flightId + " vì đã có vé được đặt.");
        }

        seatRepository.deleteByFlightId(flightId);

        flightRepository.delete(flightId);
        System.out.println("Chuyến bay " + flightId + " đã được xóa thành công.");
    }

    public boolean isPlaneScheduleOverlap(Flight newFlight) {
        return flightRepository.loadAll().stream()
                .filter(f -> f.getPlaneId().equals(newFlight.getPlaneId()))
                .filter(f -> !f.getFlightId().equals(newFlight.getFlightId()))
                .anyMatch(existingFlight -> {
                    LocalDateTime start1 = newFlight.getDepartureTime();
                    LocalDateTime end1 = newFlight.getArrivalTime();
                    LocalDateTime start2 = existingFlight.getDepartureTime();
                    LocalDateTime end2 = existingFlight.getArrivalTime();
                    return start1.isBefore(end2) && end1.isAfter(start2);
                });
    }

    private void generateSeatsForFlight(Flight flight, Plane plane) {
        List<Seat> newSeats = SeatGenerator.generateSeats(flight.getFlightId(), plane);
        seatRepository.addAll(newSeats);
    }
}