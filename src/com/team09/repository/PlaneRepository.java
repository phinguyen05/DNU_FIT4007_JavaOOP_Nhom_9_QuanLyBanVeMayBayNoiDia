package com.team09.repository;

import com.team09.models.Plane;
import java.util.Arrays;

public class PlaneRepository extends BaseRepository<Plane> {
    public PlaneRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected Plane parse(String[] f) {
        try {
            String id = f[0];
            int economy = Integer.parseInt(f[1]);
            int business = Integer.parseInt(f[2]);
            int firstClass = Integer.parseInt(f[3]);
            return new Plane(id, economy, business, firstClass);
        } catch (Exception e) {
            System.err.println("Lỗi parse Plane: " + Arrays.toString(f));
            return null;
        }
    }

    @Override
    protected String toCsv(Plane p) {
        return String.join(",",
                p.getPlaneId(),
                String.valueOf(p.getEconomySeats()),
                String.valueOf(p.getBusinessSeats()),
                String.valueOf(p.getFirstClassSeats()));
    }

    @Override
    protected String getHeader() {
        return "planeId,economySeats,businessSeats,firstClassSeats";
    }

    @Override
    protected String getId(Plane p) {
        return p.getPlaneId();
    }
}