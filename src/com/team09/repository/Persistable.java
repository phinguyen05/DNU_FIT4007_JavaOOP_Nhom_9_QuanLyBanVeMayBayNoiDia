package com.team09.repository; // Thay vì com.team09.services

import java.io.IOException;
import java.util.List;

/**
 * Interface cho các dịch vụ (Repository) có khả năng đọc/ghi dữ liệu bền vững.
 * @param <T> Kiểu đối tượng cần lưu trữ
 */
public interface Persistable<T> {
    /**
     * Tải danh sách đối tượng từ tệp.
     */
    void loadData() throws IOException;

    /**
     * Lưu danh sách đối tượng vào tệp.
     */
    void saveData() throws IOException;
}