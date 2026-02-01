package uz.zafar.onlineshoptelegrambot.util;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class Util {

    public static <T> List<T> page(List<T> list, int page, int size) {
        int from = page * size;
        int to = Math.min(from + size, list.size());

        if (from >= list.size()) {
            return List.of();
        }
        return list.subList(from, to);
    }

    public static String getBaseUrl(HttpServletRequest request) {
        String host = request.getHeader("X-Forwarded-Host");
        if (host == null || host.isBlank()) {
            host = request.getServerName();
        }
        if (host.contains(",")) {
            host = host.split(",")[0].trim();
        }
        return "https://" + host;
    }

    public static String generateSafeFileName(String originalFilename) {

        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                extension;
    }
}

