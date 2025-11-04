package com.team09.repository;

import com.team09.models.Invoice;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class InvoiceRepository extends BaseRepository<Invoice> {

    public InvoiceRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected Invoice parse(String[] f) {
        try {
            // Cấu trúc CSV: invoiceId,customerId,createdDate,totalAmount,ticketIds
            if (f.length < 5) { // Kiểm tra tối thiểu 5 cột
                System.err.println("Dữ liệu Invoice không hợp lệ (thiếu cột): " + Arrays.toString(f));
                return null;
            }
            String invoiceId = f[0].trim();
            String customerId = f[1].trim();
            // LƯU Ý: Dùng LocalDateTime.parse() mặc định cho định dạng ISO (vd: 2025-11-01T10:00)
            // Nếu file CSV dùng 2025-11-01 10:00, cần dùng DateTimeFormatter.
            // Dữ liệu mẫu dùng định dạng ISO (2025-11-01 10:00, nhưng .toString() mặc định là T).
            // Ta dùng LocalDateTime.parse(String) mặc định, và đổi lại toString() khi lưu.
            LocalDateTime createdDate = LocalDateTime.parse(f[2].trim().replace(" ", "T")); // Thêm 'T' nếu cần
            double totalAmount = Double.parseDouble(f[3].trim());
            List<String> ticketIds = new ArrayList<>();

            if (!f[4].isEmpty()) {
                // Tách danh sách vé bằng dấu `;`
                ticketIds = Arrays.asList(f[4].trim().split(";"));
            }

            return new Invoice(invoiceId, customerId, createdDate, totalAmount, ticketIds);

        } catch (Exception e) {
            System.err.println("Lỗi parse Invoice: " + Arrays.toString(f) + " -> " + e.getMessage());
            return null;
        }
    }

    @Override
    protected String toCsv(Invoice i) {
        String ticketList = (i.getTicketIds() == null || i.getTicketIds().isEmpty())
                ? ""
                : i.getTicketIds().stream().collect(Collectors.joining(";"));

        return String.join(",",
                i.getInvoiceId(),
                i.getCustomerId(),
                i.getCreatedDate().toString().replace("T", " "), // Lưu lại thành format 'yyyy-MM-dd HH:mm' cho dễ đọc
                String.valueOf(i.getTotalAmount()),
                ticketList
        );
    }

    @Override
    protected String getHeader() {
        return "invoiceId,customerId,createdDate,totalAmount,ticketIds";
    }

    @Override
    protected String getId(Invoice i) {
        return i.getInvoiceId();
    }

    // ... (Phương thức deleteByTicketId giữ nguyên) ...
}