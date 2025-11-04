package com.team09.main;

import com.team09.ui.CLIMenu;
import com.team09.repository.BaseRepository;
import com.team09.repository.CustomerRepository;
import com.team09.repository.FlightRepository;
import com.team09.repository.PlaneRepository;
import com.team09.repository.SeatRepository;
import com.team09.repository.TicketRepository;
import com.team09.repository.InvoiceRepository;
import com.team09.repository.RevenueRepository;
import com.team09.services.BookingService;
import com.team09.services.FlightManagementService; // Bổ sung
import com.team09.services.ReportService; // Bổ sung

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static List<BaseRepository<?>> repositories;

    public static void main(String[] args) {
        System.out.println("--- ✈️ ỨNG DỤNG QUẢN LÝ BÁN VÉ MÁY BAY NỘI ĐỊA ✈️ ---");

        // 1. Khởi tạo Repositories
        PlaneRepository planeRepository = new PlaneRepository("data/planes.csv");
        FlightRepository flightRepository = new FlightRepository("data/flights.csv");
        CustomerRepository customerRepository = new CustomerRepository("data/customers.csv");
        SeatRepository seatRepository = new SeatRepository("data/seats.csv");
        TicketRepository ticketRepository = new TicketRepository("data/tickets.csv");
        InvoiceRepository invoiceRepository = new InvoiceRepository("data/invoices.csv");
        RevenueRepository revenueRepository = new RevenueRepository("data/revenue.csv");

        // 2. Khởi tạo Services (DI)
        BookingService bookingService = new BookingService(
                flightRepository, seatRepository, ticketRepository, customerRepository,
                invoiceRepository, revenueRepository // Bổ sung revenueRepository
        );
        FlightManagementService flightManagementService = new FlightManagementService(
                planeRepository, flightRepository, seatRepository
        );
        ReportService reportService = new ReportService(
                invoiceRepository, ticketRepository, flightRepository, seatRepository
        );

        // Ghi lại tất cả repositories để quản lý tập trung việc Load/Save
        repositories = Arrays.asList(
                planeRepository, flightRepository, customerRepository, seatRepository,
                ticketRepository, invoiceRepository, revenueRepository
        );

        // ** TẢI DỮ LIỆU KHI KHỞI ĐỘNG **
        try {
            System.out.println("Đang tải dữ liệu từ tệp...");
            loadAllData();
            System.out.println("Tải dữ liệu thành công. Sẵn sàng khởi động.");
        } catch (Exception e) {
            System.err.println("LỖI KHỞI ĐỘNG: Không thể tải dữ liệu ban đầu. Vui lòng kiểm tra file data. " + e.getMessage());
            e.printStackTrace(); // In stack trace để dễ debug
            return; // Dừng chương trình nếu không thể tải dữ liệu
        }

        // ** THIẾT LẬP SHUTDOWN HOOK ĐỂ LƯU DỮ LIỆU KHI THOÁT **
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n--- 💾 Đang thực hiện lưu dữ liệu bền vững trước khi thoát... ---");
            saveAllData();
            System.out.println("--- Dữ liệu đã được lưu thành công. Tạm biệt! ---");
        }));

        // 3. Khởi tạo và chạy Menu (Truyền Services)
        CLIMenu menu = new CLIMenu(bookingService, flightManagementService, reportService);
        menu.run(); // Sử dụng run() thay vì start()
    }

    // Phương thức buộc tất cả repositories tải dữ liệu (Load)
    private static void loadAllData() throws IOException {
        for (BaseRepository<?> repo : repositories) {
            repo.loadAll(); // Giả định BaseRepository có phương thức loadAll() để đọc dữ liệu
        }
    }

    // Phương thức lưu tất cả repositories (Save)
    private static void saveAllData() {
        for (BaseRepository<?> repo : repositories) {
            try {
                // Ta gọi saveAll() không tham số, nó sẽ tự gọi loadAll() để lấy dữ liệu mới nhất
                repo.saveAll();
            } catch (IOException e) {
                System.err.println("LỖI LƯU DỮ LIỆU cho " + repo.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }
}