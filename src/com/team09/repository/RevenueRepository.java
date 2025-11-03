package com.team09.repository;

import com.team09.models.Revenue;
import java.util.*;

public class RevenueRepository extends BaseRepository<Revenue> {

    public RevenueRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected Revenue parse(String[] f) {
        try {
            if (f == null || f.length < 4) {
                System.err.println(" Dữ liệu Revenue không hợp lệ: " + Arrays.toString(f));
                return null;
            }

            String id = f[0].trim();
            String flightId = f[1].trim();
            double amount = Double.parseDouble(f[2].trim());
            String date = f[3].trim();

            return new Revenue(id, flightId, amount, date);

        } catch (Exception e) {
            System.err.println(" Lỗi khi parse Revenue: " + Arrays.toString(f) + " -> " + e.getMessage());
            return null;
        }
    }

    @Override
    protected String toCsv(Revenue r) {
        return String.join(",",
                r.getRevenueId(),
                r.getFlightId(),
                String.valueOf(r.getAmount()),
                r.getDate());
    }

    @Override
    protected String getHeader() {
        return "revenueId,flightId,amount,date";
    }
}
