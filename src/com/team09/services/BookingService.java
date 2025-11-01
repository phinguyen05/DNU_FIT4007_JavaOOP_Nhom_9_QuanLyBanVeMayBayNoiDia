package com.team09.main.services;

import com.team09.main.exceptions.*;
import com.team09.main.models.*;
import com.team09.main.repository.*;
import java.time.LocalDateTime;

public class BookingService {

    // ... (Các repo và constructor đã định nghĩa ở phần trước) ...
    private final FlightRepository flightRepo;
    private final SeatRepository seatRepo;
    private final TicketRepository ticketRepo;
    private final CustomerRepository customerRepo;
    private final InvoiceRepository invoiceRepo;

    private final RefundPolicy refundPolicy; // Chính sách hoàn tiền

    public BookingService(FlightRepository flightRepo, SeatRepository seatRepo,
                          TicketRepository ticketRepo, CustomerRepository customerRepo,
                          InvoiceRepository invoiceRepo) {
        this.flightRepo = flightRepo;
        this.seatRepo = seatRepo;
        //...

        // Sử dụng chính sách hoàn tiền tiêu chuẩn
        this.refundPolicy = new StandardRefundPolicy();
    }

    // ... (Hàm bookTicket() đã có) ...

    /**
     * Logic nghiệp vụ Hủy vé
     * @param ticketId ID của vé cần hủy
     * @return Số tiền được hoàn lại
     */
    public double cancelTicket(String ticketId)
            throws TicketNotFoundException, FlightNotFoundException, SeatNotFoundException, InvalidCancellationException {

        // 1. Tìm vé
        Ticket ticket = ticketRepo.findById(ticketId);
        if (ticket == null) {
            throw new TicketNotFoundException("Không tìm thấy vé: " + ticketId);
        }

        // 2. Tìm chuyến bay (để check giờ bay)
        Flight flight = flightRepo.findById(ticket.getFlightId());
        if (flight == null) {
            throw new FlightNotFoundException("Lỗi dữ liệu: Không tìm thấy chuyến bay của vé.");
        }

        // 3. Kiểm tra điều kiện hủy (không thể hủy sau khi bay)
        if (LocalDateTime.now().isAfter(flight.getDepartureTime())) {
            throw new InvalidCancellationException("Chuyến bay đã cất cánh. Không thể hủy vé.");
        }

        // 4. Tính toán tiền hoàn
        double refundAmount = refundPolicy.calculateRefund(ticket, flight);

        // 5. Cập nhật trạng thái
        // 5a. Chuyển ghế về "AVAILABLE"
        Seat seat = seatRepo.findById(ticket.getSeatId());
        if (seat != null) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seatRepo.update(seat);
        } else {
            throw new SeatNotFoundException("Lỗi dữ liệu: Không tìm thấy ghế của vé.");
        }

        // 5b. Xóa vé khỏi hệ thống (hoặc đánh dấu là CANCELED)
        ticketRepo.delete(ticketId);

        // 5c. Xóa (hoặc cập nhật) hóa đơn liên quan
        // (Tùy nghiệp vụ, có thể tạo 1 hóa đơn âm, hoặc chỉ xóa)
        invoiceRepo.deleteByTicketId(ticketId); // Giả sử có hàm này

        return refundAmount;
    }
}

// *** Bạn cần bổ sung các Exception này vào package exceptions ***
class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String msg) { super(msg); }
}
class SeatNotFoundException extends RuntimeException {
    public SeatNotFoundException(String msg) { super(msg); }
}