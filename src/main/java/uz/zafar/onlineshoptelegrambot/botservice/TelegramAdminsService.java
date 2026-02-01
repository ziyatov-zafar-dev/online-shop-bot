package uz.zafar.onlineshoptelegrambot.botservice;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface TelegramAdminsService {
    void handleUpdate(Map<String, Object> update, HttpServletRequest request) ;
}
