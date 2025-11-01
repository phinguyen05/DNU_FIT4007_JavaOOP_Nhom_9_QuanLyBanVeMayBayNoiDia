package com.team09.repository;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public abstract class BaseRepository<T> {
    protected String filePath;

    public BaseRepository(String filePath) {
        this.filePath = filePath;
    }

    // Abstract: các lớp con tự định nghĩa cách parse
    protected abstract T parse(String[] fields);
    protected abstract String toCsv(T obj);

    public List<T> loadAll() {
        List<T> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            boolean skipHeader = true;
            while ((line = br.readLine()) != null) {
                if (skipHeader) { skipHeader = false; continue; }
                String[] parts = line.split(",", -1);
                T obj = parse(parts);
                if (obj != null) list.add(obj);
            }
        } catch (IOException e) {
            System.err.println("Không đọc được file " + filePath + ": " + e.getMessage());
        }
        return list;
    }

    public void saveAll(List<T> list) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filePath))) {
            bw.write(getHeader());
            bw.newLine();
            for (T obj : list) {
                bw.write(toCsv(obj));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Lỗi ghi file " + filePath + ": " + e.getMessage());
        }
    }

    protected abstract String getHeader();
}
