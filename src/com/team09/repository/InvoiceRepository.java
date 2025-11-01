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
            // Cấu trúc CSV: invoiceId,customerId,createdDate,totalAmount,ticketIds (phân cách bằng dấu ;)
            String invoiceId = f[0];
            String customerId = f[1];
            LocalDateTime createdDate = LocalDateTime.parse(f[2]);
            double totalAmount = Double.parseDouble(f[3]);
            List<String> ticketIds = new ArrayList<>();

            if (f.length > 4 && !f[4].isEmpty()) {
                ticketIds = Arrays.asList(f[4].split(";"));
            }

            return new Invoice(invoiceId, customerId, createdDate, totalAmount, ticketIds);

        } catch (Exception e) {
            System.err.println("Lỗi parse Invoice: " + Arrays.toString(f));
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
                i.getCreatedDate().toString(),
                String.valueOf(i.getTotalAmount()),
                ticketList
        );
    }

    @Override
    protected String getHeader() {
        return "invoiceId,customerId,createdDate,totalAmount,ticketIds";
    }
}
