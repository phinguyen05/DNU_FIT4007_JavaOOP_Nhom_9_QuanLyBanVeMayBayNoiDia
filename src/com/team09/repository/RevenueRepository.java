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
            // Cấu trúc CSV: date,totalRevenue,ticketCount,type
            if (f == null || f.length < 4) {
                System.err.println(" Dữ liệu Revenue không hợp lệ: " + Arrays.toString(f));
                return null;
            }

            String date = f[0].trim();
            double totalRevenue = Double.parseDouble(f[1].trim());
            int ticketCount = Integer.parseInt(f[2].trim());
            String type = f[3].trim();

            return new Revenue(date, totalRevenue, ticketCount, type);

        } catch (Exception e) {
            System.err.println(" Lỗi khi parse Revenue: " + Arrays.toString(f) + " -> " + e.getMessage());
            return null;
        }
    }

    @Override
    protected String toCsv(Revenue r) {
        return String.join(",",
                r.getDate(),
                String.valueOf(r.getTotalRevenue()),
                String.valueOf(r.getTicketCount()),
                r.getType());
    }

    @Override
    protected String getHeader() {
        return "date,totalRevenue,ticketCount,type";
    }

    @Override
    protected String getId(Revenue r) {
        return r.getDate() + "-" + r.getType(); // Kết hợp date và type để tạo ID duy nhất
    }
}