package com.team09.models;

import java.io.Serializable;

/**
 * Lớp trừu tượng cho Người.
 */
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String fullName;
    protected String phone;
    protected String email;

    public Person(String fullName, String phone, String email) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
    }

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}