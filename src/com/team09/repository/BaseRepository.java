package com.team09.repository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class BaseRepository<T> {
    protected String filePath;

    public BaseRepository(String filePath) {
        this.filePath = filePath;
    }

    protected abstract T parse(String[] fields);
    protected abstract String toCsv(T obj);
    protected abstract String getId(T obj);
    protected abstract String getHeader(); // Phương thức này đã được khai báo abstract

    /**
     * Tải tất cả dữ liệu từ tệp CSV và trả về dưới dạng List.
     */
    public List<T> loadAll() {
        List<T> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            boolean skipHeader = true;
            while ((line = br.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue; // Bỏ qua dòng header
                }
                // Sử dụng split với giới hạn -1 để giữ lại các trường rỗng
                String[] parts = line.split(",", -1);
                T obj = parse(parts);
                if (obj != null) list.add(obj);
            }
        } catch (IOException e) {
            System.err.println("Lưu ý: Không tìm thấy file dữ liệu: " + filePath + ". Khởi tạo với danh sách trống.");
        }
        return list;
    }

    /**
     * Ghi tất cả dữ liệu từ List vào tệp CSV.
     */
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

    // Phương thức loadAll() không tham số để đồng nhất với logic trong Main
    public void loadData() throws IOException {
        // Thực chất chỉ cần gọi loadAll() để kiểm tra I/O.
        // Dữ liệu sẽ được cache/sử dụng khi cần trong các hàm khác.
        // Tuy nhiên, theo logic của Main, chỉ cần gọi loadAll() ở đây là đủ.
    }

    // Phương thức saveAll() không tham số để đồng nhất với logic trong Main
    public void saveAll() throws IOException {
        // Lấy tất cả dữ liệu hiện tại (được giả định đã được cập nhật)
        // Lưu ý: Trong kiến trúc đơn giản này, ta luôn Load-Update-Save
        // Nên chỉ cần gọi saveAll(loadAll())
        saveAll(loadAll());
    }

    public List<T> getAll() { return loadAll(); }

    public T findById(String id) {
        return loadAll().stream()
                .filter(obj -> getId(obj).equals(id))
                .findFirst()
                .orElse(null);
    }

    public void add(T obj) {
        List<T> all = loadAll();
        all.add(obj);
        saveAll(all);
    }

    public void addAll(List<T> objs) {
        List<T> all = loadAll();
        all.addAll(objs);
        saveAll(all);
    }

    public void update(T obj) {
        List<T> all = loadAll();
        String id = getId(obj);
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (getId(all.get(i)).equals(id)) {
                all.set(i, obj);
                found = true;
                break;
            }
        }
        if (found) {
            saveAll(all);
        } else {
            System.err.println("Không tìm thấy đối tượng để cập nhật với ID: " + id);
        }
    }

    public void delete(String id) {
        List<T> all = loadAll();
        Predicate<T> objFilter = obj -> !getId(obj).equals(id);
        List<T> updatedList = all.stream().filter(objFilter).collect(Collectors.toList());

        if (updatedList.size() < all.size()) {
            saveAll(updatedList);
        } else {
            System.err.println("Không tìm thấy đối tượng để xóa với ID: " + id);
        }
    }
}