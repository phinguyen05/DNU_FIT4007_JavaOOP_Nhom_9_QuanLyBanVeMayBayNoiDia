package com.team09.ui;

import com.team09.exceptions.TicketNotFoundException;
import com.team09.models.Customer; // Bổ sung
import com.team09.models.DomesticFlight;
import com.team09.models.Flight;
import com.team09.models.Plane; // Bổ sung
import com.team09.models.Seat;          // <--- Cần thêm dòng này
import com.team09.models.SeatStatus;    // <--- Cần thêm dòng này
import com.team09.models.Ticket;
import com.team09.services.BookingService;
import com.team09.services.FlightManagementService;
import com.team09.services.ReportService;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List; // Bổ sung
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;    // <--- Cần thêm dòng này
// Import này đã có: import java.util.List;
public class CLIMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final BookingService bookingService;
    private final FlightManagementService flightManagementService;
    private final ReportService reportService;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public CLIMenu(BookingService bookingService, FlightManagementService flightManagementService, ReportService reportService) {
        this.bookingService = bookingService;
        this.flightManagementService = flightManagementService;
        this.reportService = reportService;
    }

    public void run() {
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
                e.printStackTrace(); // In lỗi đầy đủ để debug
                choice = -1;
            }
        } while (choice != 0);
        System.out.println("👋 Tạm biệt!");
    }

    private void displayMenu() {
        System.out.println("\n===== HỆ THỐNG QUẢN LÝ BÁN VÉ MÁY BAY =====");
        System.out.println("1. Đặt vé mới");
        System.out.println("2. Hủy vé");
        System.out.println("3. Quản lý Chuyến bay (Tạo, Sửa, Xóa)");
        System.out.println("4. Quản lý Máy bay (Thêm, Sửa, Xóa)");
        System.out.println("5. Quản lý Khách hàng (Thêm, Sửa, Xóa, Tìm kiếm)");
        System.out.println("6. Báo cáo & Thống kê");
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
                handleFlightManagementMenu();
                break;
            case 4:
                handlePlaneManagementMenu();
                break;
            case 5:
                handleCustomerManagementMenu();
                break;
            case 6:
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
            System.out.printf("   - %s: %s -> %s (Khởi hành: %s, Giá cơ bản: %,.0f VNĐ) - Máy bay: %s\n",
                    f.getFlightId(), f.getOrigin(), f.getDestination(), f.getDepartureTime().format(DATETIME_FORMATTER), f.getBasePrice(), f.getPlaneId());
        });

        System.out.print("Nhập mã chuyến bay: ");
        String flightId = scanner.nextLine().toUpperCase();

        // Hiển thị trạng thái ghế
        Flight flight = flightManagementService.getFlightById(flightId);
        if (flight == null) {
            System.out.println("❌ Không tìm thấy chuyến bay " + flightId);
            return;
        }

        System.out.println("Trạng thái ghế trên chuyến " + flightId + ":");
        seatRepoDisplay(flightId);


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

    private void seatRepoDisplay(String flightId) {
        // Hàm hiển thị trạng thái ghế
        List<Seat> seats = reportService.getAllSeatsByFlightId(flightId);
        seats.stream()
                .collect(Collectors.groupingBy(Seat::getSeatType))
                .forEach((type, seatList) -> {
                    System.out.println("\n--- Hạng " + type + " ---");
                    seatList.stream()
                            .sorted((s1, s2) -> s1.getSeatNumber().compareTo(s2.getSeatNumber()))
                            .forEach(s -> {
                                String status = s.getStatus() == SeatStatus.BOOKED ? "❌ Đã đặt" : "✅ Trống";
                                System.out.printf(" %s (%s) ", s.getSeatNumber(), status);
                                if (Integer.parseInt(s.getSeatNumber().substring(1)) % 10 == 0) {
                                    System.out.println(); // Xuống dòng sau mỗi 10 ghế
                                }
                            });
                    System.out.println();
                });
    }

    private void handleCancelTicket() {
        System.out.println("\n--- HỦY VÉ ---");
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
    // QUẢN LÝ CHUYẾN BAY (Hoàn thiện CRUD)
    // ===================================
    private void handleFlightManagementMenu() {
        int subChoice;
        do {
            System.out.println("\n--- QUẢN LÝ CHUYẾN BAY ---");
            System.out.println("1. Danh sách chuyến bay");
            System.out.println("2. Tạo chuyến bay mới");
            System.out.println("3. Sửa thông tin chuyến bay");
            System.out.println("4. Xóa chuyến bay");
            System.out.println("0. Quay lại Menu chính");
            System.out.print("Chọn chức năng: ");
            try {
                subChoice = scanner.nextInt();
                scanner.nextLine();
                switch (subChoice) {
                    case 1:
                        displayAllFlights();
                        break;
                    case 2:
                        handleCreateFlight();
                        break;
                    case 3:
                        handleUpdateFlight();
                        break;
                    case 4:
                        handleDeleteFlight();
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Lựa chọn không tồn tại.");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Lựa chọn không hợp lệ. Vui lòng nhập số.");
                scanner.nextLine();
                subChoice = -1;
            } catch (Exception e) {
                System.out.println("❌ Lỗi: " + e.getMessage());
                subChoice = -1;
            }
        } while (subChoice != 0);
    }

    private void displayAllFlights() {
        System.out.println("\n--- DANH SÁCH CHUYẾN BAY ---");
        List<Flight> flights = flightManagementService.getAllFlights();
        if (flights.isEmpty()) {
            System.out.println("Không có chuyến bay nào.");
            return;
        }
        flights.forEach(f -> {
            System.out.printf("   - %s: %s -> %s (Khởi hành: %s) - Giá: %,.0f VNĐ - Máy bay: %s\n",
                    f.getFlightId(), f.getOrigin(), f.getDestination(), f.getDepartureTime().format(DATETIME_FORMATTER), f.getBasePrice(), f.getPlaneId());
        });
    }

    private void handleCreateFlight() throws Exception {
        System.out.println("\n--- TẠO CHUYẾN BAY MỚI ---");
        System.out.print("Nhập Mã chuyến bay (VD: VN0012): ");
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

    private void handleUpdateFlight() {
        System.out.println("\n--- SỬA CHUYẾN BAY ---");
        System.out.print("Nhập Mã chuyến bay cần sửa (VD: VN0001): ");
        String flightId = scanner.nextLine().toUpperCase();

        Flight existingFlight = flightManagementService.getFlightById(flightId);
        if (existingFlight == null) {
            System.out.println("❌ Không tìm thấy chuyến bay " + flightId);
            return;
        }

        System.out.println("Thông tin hiện tại: " + existingFlight);

        System.out.print("Nhập Điểm đi mới (Enter để bỏ qua, hiện tại: " + existingFlight.getOrigin() + "): ");
        String origin = scanner.nextLine();
        origin = origin.isEmpty() ? existingFlight.getOrigin() : origin.toUpperCase();

        System.out.print("Nhập Điểm đến mới (Enter để bỏ qua, hiện tại: " + existingFlight.getDestination() + "): ");
        String destination = scanner.nextLine();
        destination = destination.isEmpty() ? existingFlight.getDestination() : destination.toUpperCase();

        System.out.print("Nhập Mã máy bay mới (Enter để bỏ qua, hiện tại: " + existingFlight.getPlaneId() + "): ");
        String planeId = scanner.nextLine();
        planeId = planeId.isEmpty() ? existingFlight.getPlaneId() : planeId.toUpperCase();

        System.out.print("Nhập Giờ khởi hành mới (yyyy-MM-dd HH:mm, Enter để bỏ qua, hiện tại: " + existingFlight.getDepartureTime().format(DATETIME_FORMATTER) + "): ");
        String depTimeStr = scanner.nextLine();
        LocalDateTime depTime = depTimeStr.isEmpty() ? existingFlight.getDepartureTime() : LocalDateTime.parse(depTimeStr, DATETIME_FORMATTER);

        System.out.print("Nhập Giờ hạ cánh mới (yyyy-MM-dd HH:mm, Enter để bỏ qua, hiện tại: " + existingFlight.getArrivalTime().format(DATETIME_FORMATTER) + "): ");
        String arrTimeStr = scanner.nextLine();
        LocalDateTime arrTime = arrTimeStr.isEmpty() ? existingFlight.getArrivalTime() : LocalDateTime.parse(arrTimeStr, DATETIME_FORMATTER);

        System.out.print("Nhập Giá cơ bản mới (VNĐ, Enter để bỏ qua, hiện tại: " + existingFlight.getBasePrice() + "): ");
        String basePriceStr = scanner.nextLine();
        double basePrice = basePriceStr.isEmpty() ? existingFlight.getBasePrice() : Double.parseDouble(basePriceStr);

        try {
            Flight updatedFlight = new DomesticFlight(flightId, origin, destination, depTime, arrTime, basePrice, planeId);
            flightManagementService.updateFlight(updatedFlight);
        } catch (Exception e) {
            System.out.println("❌ Lỗi sửa chuyến bay: " + e.getMessage());
        }
    }

    private void handleDeleteFlight() {
        System.out.println("\n--- XÓA CHUYẾN BAY ---");
        System.out.print("Nhập Mã chuyến bay cần xóa (VD: VN0001): ");
        String flightId = scanner.nextLine().toUpperCase();
        try {
            flightManagementService.deleteFlight(flightId);
        } catch (Exception e) {
            System.out.println("❌ Lỗi xóa chuyến bay: " + e.getMessage());
        }
    }

    // ===================================
    // QUẢN LÝ MÁY BAY (Hoàn thiện CRUD)
    // ===================================

    private void handlePlaneManagementMenu() {
        int subChoice;
        do {
            System.out.println("\n--- QUẢN LÝ MÁY BAY ---");
            System.out.println("1. Danh sách máy bay");
            System.out.println("2. Thêm máy bay mới");
            System.out.println("3. Sửa thông tin máy bay");
            System.out.println("4. Xóa máy bay");
            System.out.println("0. Quay lại Menu chính");
            System.out.print("Chọn chức năng: ");
            try {
                subChoice = scanner.nextInt();
                scanner.nextLine();
                switch (subChoice) {
                    case 1:
                        displayAllPlanes();
                        break;
                    case 2:
                        handleAddPlane();
                        break;
                    case 3:
                        handleUpdatePlane();
                        break;
                    case 4:
                        handleDeletePlane();
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Lựa chọn không tồn tại.");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Lựa chọn không hợp lệ. Vui lòng nhập số.");
                scanner.nextLine();
                subChoice = -1;
            } catch (Exception e) {
                System.out.println("❌ Lỗi: " + e.getMessage());
                subChoice = -1;
            }
        } while (subChoice != 0);
    }

    private void displayAllPlanes() {
        System.out.println("\n--- DANH SÁCH MÁY BAY ---");
        List<Plane> planes = flightManagementService.getAllPlanes();
        if (planes.isEmpty()) {
            System.out.println("Không có máy bay nào.");
            return;
        }
        planes.forEach(p -> {
            System.out.printf("   - %s: Tổng ghế %d (Eco: %d, Bus: %d, First: %d)\n",
                    p.getPlaneId(), p.getTotalSeats(), p.getEconomySeats(), p.getBusinessSeats(), p.getFirstClassSeats());
        });
    }

    private void handleAddPlane() {
        System.out.println("\n--- THÊM MÁY BAY MỚI ---");
        System.out.print("Nhập Mã máy bay (VD: VN-B888): ");
        String planeId = scanner.nextLine().toUpperCase();

        try {
            System.out.print("Số ghế Phổ thông (Economy): ");
            int economy = scanner.nextInt();
            System.out.print("Số ghế Thương gia (Business): ");
            int business = scanner.nextInt();
            System.out.print("Số ghế Hạng nhất (First Class): ");
            int firstClass = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Plane newPlane = new Plane(planeId, economy, business, firstClass);
            flightManagementService.addPlane(newPlane);
        } catch (InputMismatchException e) {
            System.out.println("❌ Lỗi nhập liệu. Vui lòng nhập số nguyên cho số ghế.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("❌ Lỗi thêm máy bay: " + e.getMessage());
        }
    }

    private void handleUpdatePlane() {
        System.out.println("\n--- SỬA THÔNG TIN MÁY BAY ---");
        System.out.print("Nhập Mã máy bay cần sửa (VD: VN-A321): ");
        String planeId = scanner.nextLine().toUpperCase();

        Plane existingPlane = flightManagementService.getPlaneById(planeId);
        if (existingPlane == null) {
            System.out.println("❌ Không tìm thấy máy bay " + planeId);
            return;
        }

        try {
            System.out.printf("Số ghế Phổ thông mới (Enter để bỏ qua, hiện tại: %d): ", existingPlane.getEconomySeats());
            String ecoStr = scanner.nextLine();
            int economy = ecoStr.isEmpty() ? existingPlane.getEconomySeats() : Integer.parseInt(ecoStr);

            System.out.printf("Số ghế Thương gia mới (Enter để bỏ qua, hiện tại: %d): ", existingPlane.getBusinessSeats());
            String busStr = scanner.nextLine();
            int business = busStr.isEmpty() ? existingPlane.getBusinessSeats() : Integer.parseInt(busStr);

            System.out.printf("Số ghế Hạng nhất mới (Enter để bỏ qua, hiện tại: %d): ", existingPlane.getFirstClassSeats());
            String firstStr = scanner.nextLine();
            int firstClass = firstStr.isEmpty() ? existingPlane.getFirstClassSeats() : Integer.parseInt(firstStr);

            Plane updatedPlane = new Plane(planeId, economy, business, firstClass);
            flightManagementService.updatePlane(updatedPlane);
        } catch (NumberFormatException e) {
            System.out.println("❌ Lỗi nhập liệu. Vui lòng nhập số nguyên cho số ghế.");
        } catch (Exception e) {
            System.out.println("❌ Lỗi sửa máy bay: " + e.getMessage());
        }
    }

    private void handleDeletePlane() {
        System.out.println("\n--- XÓA MÁY BAY ---");
        System.out.print("Nhập Mã máy bay cần xóa (VD: VN-B888): ");
        String planeId = scanner.nextLine().toUpperCase();
        try {
            flightManagementService.deletePlane(planeId);
        } catch (Exception e) {
            System.out.println("❌ Lỗi xóa máy bay: " + e.getMessage());
        }
    }

    // ===================================
    // QUẢN LÝ KHÁCH HÀNG (Hoàn thiện CRUD)
    // ===================================
    private void handleCustomerManagementMenu() {
        int subChoice;
        do {
            System.out.println("\n--- QUẢN LÝ KHÁCH HÀNG ---");
            System.out.println("1. Danh sách khách hàng");
            System.out.println("2. Thêm khách hàng mới");
            System.out.println("3. Sửa thông tin khách hàng");
            System.out.println("4. Xóa khách hàng");
            System.out.println("5. Tìm kiếm khách hàng");
            System.out.println("0. Quay lại Menu chính");
            System.out.print("Chọn chức năng: ");
            try {
                subChoice = scanner.nextInt();
                scanner.nextLine();
                switch (subChoice) {
                    case 1:
                        displayAllCustomers();
                        break;
                    case 2:
                        handleAddCustomer();
                        break;
                    case 3:
                        handleUpdateCustomer();
                        break;
                    case 4:
                        handleDeleteCustomer();
                        break;
                    case 5:
                        handleSearchCustomer();
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Lựa chọn không tồn tại.");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Lựa chọn không hợp lệ. Vui lòng nhập số.");
                scanner.nextLine();
                subChoice = -1;
            } catch (Exception e) {
                System.out.println("❌ Lỗi: " + e.getMessage());
                subChoice = -1;
            }
        } while (subChoice != 0);
    }

    private void displayAllCustomers() {
        System.out.println("\n--- DANH SÁCH KHÁCH HÀNG ---");
        List<Customer> customers = bookingService.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("Không có khách hàng nào.");
            return;
        }
        customers.forEach(c -> {
            System.out.printf("   - [%s] Tên: %s, SĐT: %s, Email: %s\n",
                    c.getCustomerId(), c.getFullName(), c.getPhone(), c.getEmail());
        });
    }

    private void handleAddCustomer() {
        System.out.println("\n--- THÊM KHÁCH HÀNG MỚI ---");
        // Tạo ID tự động
        String customerId = "CUS" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        System.out.print("Nhập Họ và Tên: ");
        String fullName = scanner.nextLine();
        System.out.print("Nhập Số điện thoại: ");
        String phone = scanner.nextLine();
        System.out.print("Nhập Email: ");
        String email = scanner.nextLine();

        try {
            Customer newCustomer = new Customer(customerId, fullName, phone, email);
            bookingService.addCustomer(newCustomer);
            System.out.println("✅ Thêm khách hàng thành công. Mã khách hàng: " + customerId);
        } catch (Exception e) {
            System.out.println("❌ Lỗi thêm khách hàng: " + e.getMessage());
        }
    }

    private void handleUpdateCustomer() {
        System.out.println("\n--- SỬA THÔNG TIN KHÁCH HÀNG ---");
        System.out.print("Nhập Mã khách hàng cần sửa (VD: CUS001): ");
        String customerId = scanner.nextLine().toUpperCase();

        Customer existingCustomer = bookingService.getCustomerById(customerId);
        if (existingCustomer == null) {
            System.out.println("❌ Không tìm thấy khách hàng " + customerId);
            return;
        }

        System.out.printf("Thông tin hiện tại: Tên: %s, SĐT: %s, Email: %s\n",
                existingCustomer.getFullName(), existingCustomer.getPhone(), existingCustomer.getEmail());

        System.out.print("Nhập Họ và Tên mới (Enter để bỏ qua): ");
        String fullName = scanner.nextLine();
        fullName = fullName.isEmpty() ? existingCustomer.getFullName() : fullName;

        System.out.print("Nhập Số điện thoại mới (Enter để bỏ qua): ");
        String phone = scanner.nextLine();
        phone = phone.isEmpty() ? existingCustomer.getPhone() : phone;

        System.out.print("Nhập Email mới (Enter để bỏ qua): ");
        String email = scanner.nextLine();
        email = email.isEmpty() ? existingCustomer.getEmail() : email;

        try {
            existingCustomer.setFullName(fullName);
            existingCustomer.setPhone(phone);
            existingCustomer.setEmail(email);
            bookingService.updateCustomer(existingCustomer);
            System.out.println("✅ Cập nhật khách hàng thành công.");
        } catch (Exception e) {
            System.out.println("❌ Lỗi sửa khách hàng: " + e.getMessage());
        }
    }

    private void handleDeleteCustomer() {
        System.out.println("\n--- XÓA KHÁCH HÀNG ---");
        System.out.print("Nhập Mã khách hàng cần xóa (VD: CUS001): ");
        String customerId = scanner.nextLine().toUpperCase();
        try {
            bookingService.deleteCustomer(customerId);
            System.out.println("✅ Xóa khách hàng " + customerId + " thành công.");
        } catch (Exception e) {
            System.out.println("❌ Lỗi xóa khách hàng: " + e.getMessage());
        }
    }

    private void handleSearchCustomer() {
        System.out.println("\n--- TÌM KIẾM KHÁCH HÀNG ---");
        System.out.print("Nhập từ khóa (Tên, SĐT, hoặc Email): ");
        String keyword = scanner.nextLine();
        List<Customer> results = bookingService.searchCustomers(keyword);

        if (results.isEmpty()) {
            System.out.println("Không tìm thấy khách hàng nào phù hợp.");
            return;
        }

        System.out.println("--- KẾT QUẢ TÌM KIẾM ---");
        results.forEach(c -> {
            System.out.printf("   - [%s] Tên: %s, SĐT: %s, Email: %s\n",
                    c.getCustomerId(), c.getFullName(), c.getPhone(), c.getEmail());
        });
    }

    // ===================================
    // CHỨC NĂNG BÁO CÁO (Giữ nguyên)
    // ===================================
    private void handleReportMenu() {
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
                    scanner.nextLine();
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