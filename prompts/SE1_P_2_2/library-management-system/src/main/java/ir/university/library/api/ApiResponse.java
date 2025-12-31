package ir.university.library.api;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
    private long timestamp;

    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static ApiResponse success(Object data) {
        return new ApiResponse(true, "عملیات موفقیت‌آمیز بود", data);
    }

    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        map.put("message", message);
        map.put("data", data);
        map.put("timestamp", timestamp);
        return map;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }
}