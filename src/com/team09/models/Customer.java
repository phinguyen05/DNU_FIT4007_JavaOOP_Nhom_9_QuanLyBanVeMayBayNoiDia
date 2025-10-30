package com.team09.models;

/**
 * Lớp Khách hàng kế thừa từ Person.
 */
public class Customer extends Person {
    private static final long serialVersionUID = 1L;

    private String customerId; // UUID hoặc mã tự tăng

    public Customer(String customerId, String fullName, String phone, String email) {
        super(fullName, phone, email);
        this.customerId = customerId;
    }

    // Getter and Setter
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    @Override
    public String toString() {
        return "Customer [ID=" + customerId + ", Name=" + fullName + ", Phone=" + phone + "]";
    }
}