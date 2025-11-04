package com.team09.ui;

import com.team09.exceptions.TicketNotFoundException;
import com.team09.models.DomesticFlight;
import com.team09.models.Flight;
import com.team09.models.Ticket;
import com.team09.services.BookingService;
import com.team09.services.FlightManagementService;
import com.team09.services.ReportService;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class CLIMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final BookingService bookingService;
    private final FlightManagementService flightManagementService;
    private final ReportService reportService;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Sửa constructor để nhận Service thay vì Repository
    public CLIMenu(BookingService bookingService, FlightManagementService flightManagementService, ReportService reportService) {
        this.bookingService = bookingService;
        this.flightManagementService = flightManagementService;
        this.reportService = reportService;
    }

    public void run() { // Đổi tên thành run() để khớp với Main
        int choice;
        do {
            displayMenu();
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                handleChoice(choice);
            } catch (InputMismatchException e) {
                System.out.println("❌ Lựa chọn không hợp lệ. Vui lòng nhập số.");
                scanner.nextLine(); // clear buffer
                choice = -1; // Đảm bảo vòng lặp tiếp tục
            } catch (Exception e) {
                System.out.println("❌ Lỗi hệ thống: " + e.getMessage());
                choice = -1;
            }
        } while (choice != 0);
        System.out.println("👋 Tạm biệt!");
    }

    private void displayMenu() {
        System.out.println("\n===== HỆ THỐNG QUẢN LÝ BÁN VÉ MÁY BAY =====");
        System.out.println("1. Đặt vé mới");
        System.out.println("2. Hủy vé");
        System.out.println("3. Quản lý chuyến bay (Tạo mới)");
        System.out.println("4. Báo cáo & Thống kê");
        System.out.println("0. Thoát");
        System.out.print("Chọn chức năng: ");
    }

    private void handleChoice(int choice) throws Exception {
        switch (choice) {
            case 1:
                handleBookTicket();
                break;
            case 2:
                handleCancelTicket();
                break;
            case 3:
                handleCreateFlight();
                break;
            case 4:
                handleReportMenu();
                break;
            case 0:
                break;
            default:
                System.out.println("Lựa chọn không tồn tại.");
        }
    }

    // ===================================
    // CHỨC NĂNG ĐẶT VÉ (Hoàn thiện)
    // ===================================
    private void handleBookTicket() {
        System.out.println("\n--- ĐẶT VÉ MỚI ---");
        System.out.println("Danh sách chuyến bay có sẵn:");
        flightManagementService.getAllFlights().forEach(f -> {
            System.out.printf("   - %s: %s -> %s (Giá cơ bản: %,.0f VNĐ) - Máy bay: %s\n",
                    f.getFlightId(), f.getOrigin(), f.getDestination(), f.getBasePrice(), f.getPlaneId());
        });

        System.out.print("Nhập mã chuyến bay: ");
        String flightId = scanner.nextLine().toUpperCase();

        System.out.print("Nhập mã ghế (VD: E01, B01, F01): ");
        String seatNumber = scanner.nextLine().toUpperCase();

        System.out.print("Nhập mã khách hàng (VD: CUS001): ");
        String customerId = scanner.nextLine().toUpperCase();

        try {
            Ticket ticket = bookingService.bookTicket(flightId, seatNumber, customerId);
            System.out.printf("✅ Đặt vé thành công! Mã vé: %s. Tổng giá: %,.0f VNĐ.\n",
                    ticket.getTicketId(), ticket.getFinalPrice());
        } catch (Exception e) {
            System.out.println("❌ Lỗi đặt vé: " + e.getMessage());
        }
    }

    private void handleCancelTicket() {
        // Giữ nguyên logic đã có
        System.out.print("Nhập mã vé cần hủy (VD: TKT001): ");
        String ticketId = scanner.nextLine().toUpperCase();
        try {
            double refundAmount = bookingService.cancelTicket(ticketId);
            System.out.printf("✅ Hủy vé %s thành công. Số tiền hoàn lại: %,.0f VNĐ.\n", ticketId, refundAmount);
        } catch (TicketNotFoundException e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Lỗi hủy vé: " + e.getMessage());
        }
    }

    // ===================================
    // CHỨC NĂNG TẠO CHUYẾN BAY (Hoàn thiện)
    // ===================================
    private void handleCreateFlight() {
        System.out.println("\n--- TẠO CHUYẾN BAY MỚI ---");

        System.out.print("Nhập Mã chuyến bay (VD: VN0011): ");
        String flightId = scanner.nextLine().toUpperCase();

        System.out.print("Nhập Điểm đi (VD: HAN): ");
        String origin = scanner.nextLine().toUpperCase();

        System.out.print("Nhập Điểm đến (VD: SGN): ");
        String destination = scanner.nextLine().toUpperCase();

        System.out.print("Nhập Mã máy bay (VD: VN-A321): ");
        String planeId = scanner.nextLine().toUpperCase();

        System.out.print("Nhập Giờ khởi hành (yyyy-MM-dd HH:mm): ");
        String depTimeStr = scanner.nextLine();

        System.out.print("Nhập Giờ hạ cánh (yyyy-MM-dd HH:mm): ");
        String arrTimeStr = scanner.nextLine();

        System.out.print("Nhập Giá cơ bản (VNĐ): ");
        double basePrice = scanner.nextDouble();
        scanner.nextLine();

        try {
            LocalDateTime depTime = LocalDateTime.parse(depTimeStr, DATETIME_FORMATTER);
            LocalDateTime arrTime = LocalDateTime.parse(arrTimeStr, DATETIME_FORMATTER);

            Flight newFlight = new DomesticFlight(flightId, origin, destination, depTime, arrTime, basePrice, planeId);
            flightManagementService.createFlight(newFlight);
        } catch (InputMismatchException e) {
            System.out.println("❌ Lỗi nhập liệu. Vui lòng kiểm tra định dạng số và ngày/giờ.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("❌ Lỗi tạo chuyến bay: " + e.getMessage());
        }
    }

    private void handleReportMenu() {
        // Giữ nguyên logic đã có
        System.out.println("\n--- BÁO CÁO & THỐNG KÊ ---");
        System.out.println("1. Doanh thu theo tháng (VD: 2025-11)");
        System.out.println("2. Tỷ lệ lấp đầy ghế theo chuyến bay");
        System.out.println("3. Top 3 đường bay doanh thu cao nhất");
        System.out.print("Chọn báo cáo: ");

        try {
            int reportChoice = scanner.nextInt();
            scanner.nextLine();

            switch (reportChoice) {
                case 1:
                    System.out.print("Nhập năm (VD: 2025): ");
                    int year = scanner.nextInt();
                    System.out.print("Nhập tháng (1-12): ");
                    int month = scanner.nextInt();
                    double revenue = reportService.getRevenueByMonth(year, Month.of(month));
                    System.out.printf("💰 Doanh thu tháng %d/%d là: %,.0f VNĐ\n", month, year, revenue);
                    break;
                case 2:
                    System.out.print("Nhập ID chuyến bay (VD: VN0001): ");
                    String flightId = scanner.nextLine().toUpperCase();
                    Map<String, Double> rates = reportService.getOccupancyRateByFlight(flightId);
                    System.out.printf("📊 Tỷ lệ lấp đầy chuyến %s:\n", flightId);
                    System.out.printf("   - Đã đặt: %.2f%%\n", rates.get("BOOKED_RATE"));
                    System.out.printf("   - Còn trống: %.2f%%\n", rates.get("AVAILABLE_RATE"));
                    break;
                case 3:
                    System.out.println("🏆 Top 3 Đường bay Doanh thu cao nhất:");
                    reportService.getTop3RoutesByRevenue().forEach(entry -> {
                        System.out.printf("   - %s: %,.0f VNĐ\n", entry.getKey(), entry.getValue());
                    });
                    break;
                default:
                    System.out.println("Lựa chọn không tồn tại.");
            }
        } catch (InputMismatchException e) {
            System.out.println("❌ Lựa chọn không hợp lệ. Vui lòng nhập số.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("❌ Lỗi báo cáo: " + e.getMessage());
        }
    }
}