package com.team09.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Triển khai chính sách hoàn tiền tiêu chuẩn theo yêu cầu đề tài.
 */
public class StandardRefundPolicy implements RefundPolicy {

    @Override
    public double calculateRefund(double originalPrice, LocalDateTime flightDateTime, LocalDateTime cancellationTime) {
        long hoursBeforeFlight = ChronoUnit.HOURS.between(cancellationTime, flightDateTime);

        if (hoursBeforeFlight < 0) {
            // Đã cất cánh
            return 0.0;
        } else if (hoursBeforeFlight < 48) {
            // Hủy vé trước giờ bay < 48h
            return originalPrice * 0.50; // Hoàn 50%
        } else {
            // Hủy vé trước giờ bay >= 48h
            return originalPrice * 0.90; // Hoàn 90%
        }
    }
}