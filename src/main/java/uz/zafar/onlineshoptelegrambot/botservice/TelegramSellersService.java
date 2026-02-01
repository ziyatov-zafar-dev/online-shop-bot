package uz.zafar.onlineshoptelegrambot.botservice;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;


public interface TelegramSellersService {

    void handleUpdate(Map<String, Object> update, HttpServletRequest request) throws Exception;

}
