package com.team09.services;

import java.time.LocalDateTime;

/**
 * Interface định nghĩa chính sách hoàn tiền.
 */
public interface RefundPolicy {
    /**
     * Tính toán số tiền hoàn lại dựa trên giá vé gốc và thời điểm hủy.
     *
     * @param originalPrice   Giá vé gốc
     * @param flightDateTime  Giờ khởi hành của chuyến bay
     * @param cancellationTime Giờ hủy vé
     * @return Số tiền được hoàn lại
     */
    double calculateRefund(double originalPrice, LocalDateTime flightDateTime, LocalDateTime cancellationTime);
}