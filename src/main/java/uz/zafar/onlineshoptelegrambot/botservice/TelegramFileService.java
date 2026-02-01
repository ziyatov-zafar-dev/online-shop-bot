package uz.zafar.onlineshoptelegrambot.botservice;

import java.io.IOException;

public interface TelegramFileService {
    String saveFileByFileId(String fileId, String botToken) throws IOException;
}
