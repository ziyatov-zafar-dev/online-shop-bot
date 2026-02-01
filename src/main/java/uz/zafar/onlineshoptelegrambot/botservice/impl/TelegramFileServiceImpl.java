package uz.zafar.onlineshoptelegrambot.botservice.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uz.zafar.onlineshoptelegrambot.botservice.TelegramFileService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDateTime;

@Service
public class TelegramFileServiceImpl implements TelegramFileService {
    public String saveFileByFileId(String fileId, String botToken) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        // 1️⃣ getFile API
        String getFileUrl =
                "https://api.telegram.org/bot" + botToken + "/getFile?file_id=" + fileId;

        String filePath;
        try (InputStream in = new URL(getFileUrl).openStream()) {

            JsonNode root = mapper.readTree(in);

            if (!root.path("ok").asBoolean()) {
                throw new IOException("Telegram getFile failed for fileId=" + fileId);
            }

            filePath = root.path("result").path("file_path").asText(null);
        }

        if (filePath == null) {
            throw new IOException("file_path not found for fileId=" + fileId);
        }

        // 2️⃣ Telegram file download URL
        String downloadUrl =
                "https://api.telegram.org/file/bot" + botToken + "/" + filePath;

        // 3️⃣ Server path
        LocalDateTime now = LocalDateTime.now();
        String extension = filePath.contains(".")
                ? filePath.substring(filePath.lastIndexOf('.'))
                : ".dat";

        Path savePath = Paths.get(
                "uploads",
                "telegram-files",
                String.valueOf(now.getYear()),
                String.format("%02d", now.getMonthValue()),
                String.format("%02d", now.getDayOfMonth()),
                "file_" + System.currentTimeMillis() + extension
        );
        Files.createDirectories(savePath.getParent());
        try (InputStream in = new URL(downloadUrl).openStream()) {
            Files.copy(in, savePath, StandardCopyOption.REPLACE_EXISTING);
        }
        return savePath.toString();
    }

}
