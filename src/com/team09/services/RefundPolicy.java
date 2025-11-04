package com.team09.services;

import com.team09.models.Flight;
import com.team09.models.Ticket;

/**
 * Interface định nghĩa chính sách hoàn tiền.
 */
public interface RefundPolicy {
    double calculateRefund(Ticket ticket, Flight flight);
}