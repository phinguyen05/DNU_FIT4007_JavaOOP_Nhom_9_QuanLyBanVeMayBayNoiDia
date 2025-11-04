package com.team09.repository;

import com.team09.models.Customer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerRepository extends BaseRepository<Customer> {

    public CustomerRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected Customer parse(String[] f) {
        try {
            // Cấu trúc CSV: customerId,fullName,phone,email
            if (f == null || f.length < 4) {
                System.err.println("Dữ liệu Customer không hợp lệ: " + Arrays.toString(f));
                return null;
            }
            return new Customer(
                    f[0].trim(), // customerId
                    f[1].trim(), // fullName
                    f[2].trim(), // phone
                    f[3].trim()  // email
            );
        } catch (Exception e) {
            System.err.println("Lỗi parse Customer: " + Arrays.toString(f) + " -> " + e.getMessage());
            return null;
        }
    }

    @Override
    protected String toCsv(Customer customer) {
        return String.join(",",
                customer.getCustomerId(),
                customer.getFullName(),
                customer.getPhone(),
                customer.getEmail()
        );
    }

    @Override
    protected String getHeader() {
        return "customerId,fullName,phone,email";
    }

    @Override
    protected String getId(Customer customer) {
        // Sửa lỗi: dùng customer.getCustomerId() thay vì customer.getId()
        return customer.getCustomerId();
    }

    /**
     * Tìm kiếm khách hàng theo tên, số điện thoại hoặc email (tìm kiếm tương đối).
     * @param keyword Từ khóa tìm kiếm.
     * @return Danh sách khách hàng phù hợp.
     */
    public List<Customer> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return loadAll(); // Trả về tất cả nếu từ khóa trống
        }
        final String lowerCaseKeyword = keyword.trim().toLowerCase();

        return loadAll().stream()
                .filter(customer -> customer.getFullName().toLowerCase().contains(lowerCaseKeyword) ||
                        customer.getPhone().contains(lowerCaseKeyword) ||
                        customer.getEmail().toLowerCase().contains(lowerCaseKeyword))
                .collect(Collectors.toList());
    }
}