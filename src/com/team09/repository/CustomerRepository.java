package com.team09.repository;

import com.team09.models.Customer;
import java.util.*;

public class CustomerRepository extends BaseRepository<Customer> {
    public CustomerRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected Customer parse(String[] f) {
        if (f.length < 4) return null;
        return new Customer(f[0], f[1], f[2], f[3]);
    }

    @Override
    protected String toCsv(Customer c) {
        return String.join(",",
                c.getCustomerId(),
                c.getFullName(),
                c.getPhone(),
                c.getEmail()
        );
    }

    @Override
    protected String getHeader() {
        return "customerId,name,phone,email";
    }
}
