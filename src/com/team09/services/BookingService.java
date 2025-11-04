package com.team09.services;

import com.team09.exceptions.*;
import com.team09.models.*;
import com.team09.repository.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

public class BookingService {

    private final FlightRepository flightRepo;
    private final SeatRepository seatRepo;
    private final TicketRepository ticketRepo;
    private final CustomerRepository customerRepo;
    private final InvoiceRepository invoiceRepo;
    private final RefundPolicy refundPolicy;
    private final RevenueRepository revenueRepo; // Bổ sung

    public BookingService(FlightRepository flightRepo, SeatRepository seatRepo,
                          TicketRepository ticketRepo, CustomerRepository customerRepo,
                          InvoiceRepository invoiceRepo, RevenueRepository revenueRepo) {
        this.flightRepo = flightRepo;
        this.seatRepo = seatRepo;
        this.ticketRepo = ticketRepo;
        this.customerRepo = customerRepo;
        this.invoiceRepo = invoiceRepo;
        this.refundPolicy = new StandardRefundPolicy();
        this.revenueRepo = revenueRepo; // Khởi tạo
    }

    /**
     * Logic nghiệp vụ Đặt vé
     */
    public Ticket bookTicket(String flightId, String seatNumber, String customerId)
            throws FlightNotFoundException, SeatNotFoundException, CustomerNotFoundException, SeatAlreadyBookedException {

        Flight flight = flightRepo.findById(flightId);
        if (flight == null) {
            throw new FlightNotFoundException("Không tìm thấy chuyến bay với mã: " + flightId);
        }

        Customer customer = customerRepo.findById(customerId);
        if (customer == null) {
            throw new CustomerNotFoundException("Không tìm thấy khách hàng với mã: " + customerId);
        }

        String seatKey = flightId + "-" + seatNumber;
        Seat seat = seatRepo.findById(seatKey);
        if (seat == null) {
            throw new SeatNotFoundException("Ghế " + seatNumber + " không tồn tại trên chuyến bay " + flightId + ".");
        }

        if (seat.getStatus() == SeatStatus.BOOKED) {
            throw new SeatAlreadyBookedException("Ghế " + seatNumber + " đã được đặt.");
        }

        // 1. TÍNH TỔNG GIÁ VÉ
        double finalPrice = flight.getBasePrice() + seat.getSurcharge();

        // 2. TẠO VÉ
        String ticketId = "TKT" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        LocalDateTime bookingTime = LocalDateTime.now();
        Ticket newTicket = new Ticket(ticketId, flightId, customerId, seatNumber, finalPrice, bookingTime);

        // 3. CẬP NHẬT TRẠNG THÁI GHẾ
        seat.setStatus(SeatStatus.BOOKED);
        seatRepo.update(seat);

        // 4. LƯU VÉ
        ticketRepo.add(newTicket);

        // 5. TẠO HÓA ĐƠN
        String invoiceId = "INV" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        Invoice invoice = new Invoice(invoiceId, customerId, bookingTime, finalPrice, Collections.singletonList(ticketId));
        invoiceRepo.add(invoice);

        // 6. CẬP NHẬT DOANH THU (Nếu cần, có thể làm trong ReportService, nhưng thực hiện ở đây là tối ưu)
        // Đây là bước không bắt buộc theo đề nhưng nên có để dữ liệu revenue.csv hoạt động
        // (BỎ QUA cho mục đích hoàn thiện tối thiểu, tập trung vào CRUD và Report)

        return newTicket;
    }

    /**
     * Logic nghiệp vụ Hủy vé
     */
    public double cancelTicket(String ticketId)
            throws TicketNotFoundException, FlightNotFoundException, SeatNotFoundException, InvalidCancellationException {

        Ticket ticket = ticketRepo.findById(ticketId);
        if (ticket == null) {
            throw new TicketNotFoundException("Không tìm thấy vé: " + ticketId);
        }

        Flight flight = flightRepo.findById(ticket.getFlightId());
        if (flight == null) {
            throw new FlightNotFoundException("Lỗi dữ liệu: Không tìm thấy chuyến bay của vé.");
        }

        if (LocalDateTime.now().isAfter(flight.getDepartureTime())) {
            throw new InvalidCancellationException("Chuyến bay đã cất cánh. Không thể hủy vé.");
        }

        double refundAmount = refundPolicy.calculateRefund(ticket, flight);

        String seatKey = ticket.getFlightId() + "-" + ticket.getSeatNumber();
        Seat seat = seatRepo.findById(seatKey);
        if (seat != null) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seatRepo.update(seat);
        } else {
            throw new SeatNotFoundException("Lỗi dữ liệu: Không tìm thấy ghế của vé.");
        }

        ticketRepo.delete(ticketId);
        // Thay vì delete hóa đơn, ta tạo hóa đơn HOÀN TIỀN
        String refundInvoiceId = "REF" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        Invoice refundInvoice = new Invoice(refundInvoiceId, ticket.getCustomerId(), LocalDateTime.now(), -refundAmount, Collections.singletonList(ticketId));
        invoiceRepo.add(refundInvoice);

        // Cập nhật lại hóa đơn cũ (tùy kiến trúc, tạm bỏ qua)
        // invoiceRepo.deleteByTicketId(ticketId); // Xóa hóa đơn mua cũ (nên tạo hóa đơn hoàn tiền mới)

        return refundAmount;
    }
}