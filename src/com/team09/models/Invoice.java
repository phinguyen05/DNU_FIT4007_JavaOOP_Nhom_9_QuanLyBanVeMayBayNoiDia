package com.team09.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Lớp Hóa đơn.
 */
public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;

    private String invoiceId;
    private String customerId;
    private LocalDateTime createdDate;
    private double totalAmount;
    private List<String> ticketIds; // Danh sách các mã vé trong hóa đơn này

    public Invoice(String invoiceId, String customerId, LocalDateTime createdDate, double totalAmount, List<String> ticketIds) {
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.createdDate = createdDate;
        this.totalAmount = totalAmount;
        this.ticketIds = ticketIds;
    }

    // Getters
    public String getInvoiceId() { return invoiceId; }
    public String getCustomerId() { return customerId; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public double getTotalAmount() { return totalAmount; }
    public List<String> getTicketIds() { return ticketIds; }
}